package launchpad.security.user

import groovy.time.TimeCategory
import launchpad.account.VerifyMethod
import launchpad.account.VerifyType
import launchpad.error.InvalidVerificationCodeException
import launchpad.error.UnknownIdentifierException
import launchpad.error.VerifyCodeExpiredException
import launchpad.mail.MailMessage
import launchpad.mail.MailService
import launchpad.security.SecurityCode
import launchpad.sms.SmsMessage
import launchpad.sms.SmsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

import javax.validation.constraints.NotNull

@Service
@Validated
class UserVerifyService {
    private static final Logger LOG = LoggerFactory.getLogger(UserVerifyService)

    @Value('${verify-code-expire-minutes}')
    private static int verifyCodeExpires

    @Value('${app.name}')
    private static String appName

    private final UserService userService
    private final MailService mailService
    private final SmsService smsService

    @Autowired
    UserVerifyService(UserService userService, MailService mailService, SmsService smsService) {
        this.userService = userService
        this.mailService = mailService
        this.smsService = smsService
    }

    List<VerifyMethod> getVerifyMethodsForCurrentUser() {
        User user = userService.loggedInUser
        List<VerifyMethod> verifyMethods = []
        if (user.email) {
            VerifyMethod verifyMethod = new VerifyMethod()
            verifyMethod.label = user.maskedEmail
            verifyMethod.verifyType = VerifyType.EMAIL
            verifyMethods.add(verifyMethod)
        }
        user.phones?.each { userPhone ->
            VerifyMethod verifyMethod = new VerifyMethod()
            verifyMethod.label = userPhone.maskedPhoneNumber
            verifyMethod.value = userPhone.id
            verifyMethod.verifyType = VerifyType.TEXT
            verifyMethods.add(verifyMethod)
        }
        return verifyMethods
    }

    boolean verifyCurrentUser(@NotNull String code) {
        User user = userService.loggedInUser
        if (!user.isVerifyRequired) {
            LOG.info('User is already verified. Skipping user verification.')
            return true
        }
        if (user.verifyCodeExpired) {
            throw new VerifyCodeExpiredException()
        }
        if (user.verifyCode != code?.trim()) {
            throw new InvalidVerificationCodeException()
        }
        user.with {
            verifyCode = null
            verifyCodeExpiresOn = null
            isVerifyRequired = false
        }
        userService.save(user)
        return true
    }

    void sendVerifyCodeToCurrentUser(VerifyMethod verifyMethod) {
        User user = userService.loggedInUser
        if (verifyMethod.verifyType == VerifyType.EMAIL) {
            sendVerifyCodeToEmail(user)
        }
        if (verifyMethod.verifyType == VerifyType.TEXT) {
            sendVerifyCodeToPhoneNumber(user, verifyMethod.value as long)
        }
    }

    void sendVerifyCodeToEmail(@NotNull User user) {
        user.verifyCode = SecurityCode.userVerifyCode
        use(TimeCategory) {
            user.verifyCodeExpiresOn = new Date() + verifyCodeExpires.minutes
        }
        MailMessage mailMessage = getVerifyCodeEmailMessage(user)
        mailService.send(mailMessage)
        userService.save(user)
    }

    void sendVerifyCodeToPhoneNumber(@NotNull User user, @NotNull long userPhoneId) {
        UserPhone userPhone = user.phones?.find { it.id == userPhoneId }
        if (!userPhone) {
            throw new UnknownIdentifierException("Provided phone number doesn't exist")
        }
        user.verifyCode = SecurityCode.userVerifyCode
        use(TimeCategory) {
            user.verifyCodeExpiresOn = new Date() + verifyCodeExpires.minutes
        }
        SmsMessage smsMessage = new SmsMessage()
        smsMessage.to = user.phones.find { it.id == userPhoneId }
        smsMessage.text = "Your ${appName} verification code is: ${user.verifyCode}"
        smsService.send(smsMessage)
        userService.save(user)
    }

    private static MailMessage getVerifyCodeEmailMessage(@NotNull User user) {
        MailMessage mailMessage = new MailMessage()
        mailMessage.to = user.email
        mailMessage.model = ['user':user]
        mailMessage.subject = "${appName} Verification Code"
        mailMessage.template = 'account-verification.ftl'
        return mailMessage
    }
}
