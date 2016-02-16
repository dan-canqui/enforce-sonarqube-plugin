/*
 * The MIT License
 *
 * Copyright 2016 Fundacion Jala.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fundacionjala.enforce.sonarqube.apex;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import org.fundacionjala.enforce.sonarqube.apex.cpd.ApexCpdMapping;
import org.fundacionjala.enforce.sonarqube.ui.ApexWidget;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

/**
 *
 */
public class ApexPlugin extends SonarPlugin {

    public static final String FILE_SUFFIXES_KEY = "sonar.apex.file.suffixes";

    @Override
    public List getExtensions() {
        return ImmutableList.of(
                PropertyDefinition.builder(FILE_SUFFIXES_KEY)
                .name("File Suffixes")
                .description("Comma-separated list of suffixes of apex files to analyze.")
                .category("apex")
                .onQualifiers(Qualifiers.PROJECT)
                .defaultValue("cls")
                .build(),

                Apex.class,
                ApexCpdMapping.class,

                ApexProfile.class,

                ApexSquidSensor.class,
                ApexRuleRepository.class,

                ApexWidget.class);
    }
}
