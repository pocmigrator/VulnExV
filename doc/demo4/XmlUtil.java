/**
 *   Copyright 2019 Yanzheng (https://github.com/micyo202). All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lion.common.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * XmlUtil
 * Xml工具类
 *
 * @author Yanzheng (https://github.com/micyo202)
 * @date 2020/6/15
 */
public class XmlUtil {

    public static <T> T xml2Obj(String xml, Class<T> clazz) {
        return xml2Obj(xml, clazz, clazz.getSimpleName());
    }

    public static <T> T xml2Obj(String xml, Class<T> clazz, String rootElement) {
        XStream xstream = new XStream();
        xstream.fromXML("<void/>");

        XStream xStream = getInstance();
        xStream.processAnnotations(clazz);
        xStream.alias(rootElement, clazz);
        xStream.ignoreUnknownElements();
        Object object = xStream.fromXML(xml);
        return clazz.cast(object);
    }

    public static <T> T xml2Obj(File xml, Class<T> clazz) {
        return xml2Obj(xml, clazz, clazz.getSimpleName());
    }

    public static <T> T xml2Obj(File xml, Class<T> clazz, String rootElement) {
        XStream xStream = getInstance();
        xStream.processAnnotations(clazz);
        xStream.alias(rootElement, clazz);
        xStream.ignoreUnknownElements();
        Object object = xStream.fromXML(xml);
        return clazz.cast(object);
    }


}