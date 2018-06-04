/*
 * Copyright 2017 Lighthouse Software, Inc.   http://www.LighthouseSoftware.com
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package voyage.security.user

import org.springframework.http.HttpStatus
import spock.lang.Specification

class PasswordResetTokenExpiredExceptionSpec extends Specification {

    def 'default exception creates a 400 Bad Request exception'() {
        when:
            PasswordResetTokenExpiredException ex = new PasswordResetTokenExpiredException()

        then:
            ex.httpStatus == HttpStatus.BAD_REQUEST
            ex.errorCode == '400_password_reset_token_expired'
            ex.message == 'Password reset token has expired'
    }

    def 'Override the exception message only affects the description'() {
        when:
            PasswordResetTokenExpiredException ex = new PasswordResetTokenExpiredException('TEST MESSAGE')

        then:
            ex.httpStatus == HttpStatus.BAD_REQUEST
            ex.errorCode == '400_password_reset_token_expired'
            ex.message == 'TEST MESSAGE'
    }
}
