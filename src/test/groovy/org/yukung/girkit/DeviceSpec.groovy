/*
 * Copyright 2015 Yusuke Ikeda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yukung.girkit

import groovy.json.JsonSlurper
import spock.lang.IgnoreIf
import spock.lang.Specification
import wslite.rest.RESTClientException

/**
 * @author yukung
 */
class DeviceSpec extends Specification {

    @IgnoreIf({ env.CI })
    def "should find IRKit within the same LAN"() {
        when:
        def devices = Device.find()

        then:
        devices.class == ArrayList

        and:
        devices.each { device ->
            device.class == Device
        }
    }

    @IgnoreIf({ env.CI })
    def "should get messages"() {
        given:
        def device = Device.find().first()

        when:
        def msg = device.getMessages()

        then:
        msg != null

        and:
        msg.format == 'raw'

        and:
        msg.freq == 38

        and:
        msg.data.class == ArrayList
    }

    @IgnoreIf({ env.CI })
    def "should post messages"() {
        given:
        def device = Device.find().first()
        def msg = new JsonSlurper().parse(getClass().getResource('/test.json'))

        when:
        device.postMessages(msg)

        then:
        notThrown(RESTClientException)
    }

    @IgnoreIf({ env.CI })
    def "should get token"() {
        given:
        def device = Device.find().first()

        when:
        def token = device.getToken()

        then:
        token.class == String

        and:
        token ==~ /^[0-9A-Z]+$/
    }

    @IgnoreIf({ env.CI })
    def "should get clientkey and deviceid"() {
        given:
        def device = Device.find().first()
        def token = device.getToken()

        when:
        def res = device.getClientKeyAndDeviceId(token)

        then:
        res != null

        and:
        res.deviceid.class == String

        and:
        res.deviceid ==~ /^[0-9A-Z]+$/

        and:
        res.clientkey.class == String

        and:
        res.clientkey ==~ /^[0-9A-Z]+$/
    }

    def "should throw IllegalArgumentException with invalid token"() {
        given:
        def device = new Device(address: Inet4Address.getByName("127.0.0.1"))   // fake device
        def token = 123456789012345

        when:
        device.getClientKeyAndDeviceId(token)

        then:
        thrown(IllegalArgumentException)
    }
}
