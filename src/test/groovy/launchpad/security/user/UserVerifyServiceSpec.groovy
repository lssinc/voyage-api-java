package launchpad.security.user

import launchpad.account.VerifyMethod
import launchpad.account.VerifyType
import launchpad.error.InvalidVerificationCodeException
import launchpad.error.UnknownIdentifierException
import launchpad.error.VerifyCodeExpiredException
import launchpad.mail.MailService
import launchpad.sms.SmsService
import spock.lang.Specification

class UserVerifyServiceSpec extends Specification {
    User user
    UserService userService = Mock()
    MailService mailService = Mock()
    SmsService smsService  = Mock()
    UserVerifyService userVerifyService = new UserVerifyService(userService, mailService, smsService)

    def setup() {
        user = new User(firstName:'Test1', lastName:'User', username:'username', email:'test@test.com', password:'password')
        UserPhone userPhone = new UserPhone(id:1, phoneType:PhoneType.HOME, phoneNumber:'9999999999', user:user)
        Set<UserPhone> userPhones = []
        userPhones.add(userPhone)
        user.phones = userPhones
    }

    def 'getVerifyMethodsForCurrentUser - validate verify method for current user' () {
        setup:
            userService.loggedInUser >> user
        when:
            List<VerifyMethod> verifyMethods = userVerifyService.verifyMethodsForCurrentUser
        then:
            2 == verifyMethods.size()
            'te**@test.com' == verifyMethods.get(0).label
            verifyMethods.get(0).verifyType == VerifyType.EMAIL
            verifyMethods.get(0).verifyType != VerifyType.TEXT
    }

    def 'verifyCurrentUser - validate user is already verified' () {
        setup:
            user = new User(firstName:'Test1', lastName:'User', username:'username', email:'test@test.com', password:'password',
                    isVerifyRequired:false,)
            userService.loggedInUser >> user
        when:
            boolean isVerifyCurrentUser = userVerifyService.verifyCurrentUser('code')
        then:
            true == isVerifyCurrentUser
    }

    def 'verifyCurrentUser - validate VerifyCodeExpiredException' () {
        setup:
            user = new User(firstName:'Test1', lastName:'User', username:'username', email:'test@test.com', password:'password',
                    isVerifyRequired:true, verifyCodeExpiresOn:new Date() - 1,)
            userService.loggedInUser >> user
        when:
             userVerifyService.verifyCurrentUser('code')
        then:
            thrown(VerifyCodeExpiredException)
    }

    def 'verifyCurrentUser - validate InvalidVerificationCodeException' () {
        setup:
            user = new User(firstName:'Test1', lastName:'User', username:'username', email:'test@test.com', password:'password',
                    isVerifyRequired:true,)
            userService.loggedInUser >> user
        when:
            userVerifyService.verifyCurrentUser('code')
        then:
            thrown(InvalidVerificationCodeException)
    }

    def 'sendVerifyCodeToCurrentUser - validate sendVerifyCodeToEmail' () {
        setup:
            VerifyMethod verifyMethod = new VerifyMethod()
            verifyMethod.label = user.maskedEmail
            verifyMethod.verifyType = VerifyType.EMAIL
            userService.loggedInUser >> user
        when:
            userVerifyService.sendVerifyCodeToCurrentUser(verifyMethod)
        then:
            user.verifyCode
            user.verifyCodeExpiresOn.format('MM/DD/YYYY') == new Date().format('MM/DD/YYYY')
    }

    def 'sendVerifyCodeToCurrentUser - validate sendVerifyCodeToPhone' () {
        setup:

            VerifyMethod verifyMethod = new VerifyMethod()
            verifyMethod.label = user.phones[0].maskedPhoneNumber
            verifyMethod.value = user.phones[0].id
            verifyMethod.verifyType = VerifyType.TEXT
            userService.loggedInUser >> user
        when:
            userVerifyService.sendVerifyCodeToCurrentUser(verifyMethod)
        then:
            user.verifyCode
            user.verifyCodeExpiresOn.format('MM/DD/YYYY') == new Date().format('MM/DD/YYYY')
    }

    def 'sendVerifyCodeToCurrentUser - validate sendVerifyCodeToPhone with UnknownIdentifierException' () {
        setup:
            VerifyMethod verifyMethod = new VerifyMethod()
            verifyMethod.label = user.phones[0].maskedPhoneNumber
            verifyMethod.value = '2'
            verifyMethod.verifyType = VerifyType.TEXT
            userService.loggedInUser >> user
        when:
            userVerifyService.sendVerifyCodeToCurrentUser(verifyMethod)
        then:
            thrown(UnknownIdentifierException)
    }

}
