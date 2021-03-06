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

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.Immutable
import wslite.rest.ContentType
import wslite.rest.RESTClient

/**
 * @author yukung
 */
@Immutable
class InternetAPI {
    String clientKey, deviceId

    private static url() {
        "https://api.getirkit.com/1/"
    }

    @Override
    String toString() {
        return "<${this.class.name} deviceid=\"${deviceId[0..6]}XXXXX\" clientkey=\"${clientKey[0..6]}XXXXX\">"
    }

    def getMessages(query = [:]) {
        query << [clientkey: clientKey]
        def client = new RESTClient(url())
        def res = client.get(path: 'messages', query: query)
        res.data.size() > 0 ? new JsonSlurper().parse(res.data) : [:]
    }

    def postMessages(irData) {
        def body = [deviceid: deviceId, clientkey: clientKey, message: JsonOutput.toJson(irData)]
        def client = new RESTClient(url())
        client.post(path: 'messages') {
            type ContentType.URLENC
            urlenc body
        }
    }
}
