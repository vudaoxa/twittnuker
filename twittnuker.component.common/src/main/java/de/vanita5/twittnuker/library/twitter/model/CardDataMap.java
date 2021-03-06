/*
 *          Twittnuker - Twitter client for Android
 *
 *          This program incorporates a modified version of
 *          Twidere - Twitter client for Android
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.vanita5.twittnuker.library.twitter.model;

import com.bluelinelabs.logansquare.LoganSquare;
import com.fasterxml.jackson.core.JsonGenerator;

import org.mariotaku.restfu.RestConverter;
import org.mariotaku.restfu.http.ValueMap;
import org.mariotaku.restfu.http.mime.Body;
import org.mariotaku.restfu.http.mime.StringBody;

import de.vanita5.twittnuker.library.MicroBlogException;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CardDataMap implements ValueMap {
    protected final Map<String, String> map = new LinkedHashMap<>();

    public void putString(String key, String value) {
        map.put("twitter:string:" + key, value);
    }

    public void putLong(String key, long value) {
        map.put("twitter:long:" + key, String.valueOf(value));
    }

    @Override
    public boolean has(String key) {
        return map.containsKey(key);
    }

    @Override
    public Object get(String key) {
        return map.get(key);
    }

    @Override
    public String[] keys() {
        final Set<String> keySet = map.keySet();
        return keySet.toArray(new String[keySet.size()]);
    }

    @Override
    public String toString() {
        return "CardDataMap{" +
                "map=" + map +
                '}';
    }

    public static class BodyConverter implements RestConverter<CardDataMap, Body, MicroBlogException> {
        @Override
        public Body convert(CardDataMap obj) throws ConvertException, IOException, MicroBlogException {
            final StringWriter sw = new StringWriter();
            final JsonGenerator generator = LoganSquare.JSON_FACTORY.createGenerator(sw);
            generator.writeStartObject();
            for (Map.Entry<String, String> entry : obj.map.entrySet()) {
                generator.writeStringField(entry.getKey(), entry.getValue());
            }
            generator.writeEndObject();
            generator.flush();
            return new StringBody(sw.toString(), Charset.defaultCharset());
        }
    }
}