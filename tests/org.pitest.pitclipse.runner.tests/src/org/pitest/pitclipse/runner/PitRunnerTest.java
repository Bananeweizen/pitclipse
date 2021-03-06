/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.runner;

import com.google.common.collect.ImmutableList;

import org.eclipse.core.runtime.Platform;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.pitest.mutationtest.MutationResultListenerFactory;
import org.pitest.util.ServiceLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.eclipse.core.runtime.FileLocator.getBundleFile;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behavior of a {@link PitRunner}'s functions.
 */
// TODO Add more tests to ensure outputs generated by Pitest are OK
public class PitRunnerTest {

    private static final String TEST_CLASS = PitOptionsTest.class.getCanonicalName();
    private static final List<String> CLASS_PATH = ImmutableList.of("org.pitest.pitclipse.runner.*");
    private static final List<String> PROJECTS = ImmutableList.of("project1", "project2");

    @Test
    public void shouldRunPitest() throws IOException {
        PitRequest request = PitRequest.builder().withPitOptions(options()).withProjects(PROJECTS).build();
        PitResults results = PitRunner.executePit().apply(request);
        assertThat(results, is(notNullValue()));
        assertThat(results.getHtmlResultFile(), is(aFileThatExists()));
        assertThat(results.getMutations(), is(notNullValue()));
        assertThat(results, is(serializable()));
    }
    
    @Test 
    public void shouldFindAllAvailableMutationResultListeners() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Collection<MutationResultListenerFactory> factories = ServiceLoader.load(MutationResultListenerFactory.class, contextClassLoader);
        Set<String> factoriesName = factories.stream().map(f -> f.name()).collect(toSet());
        
        String[] expectedFactoriesName = new String[] {"HTML", "CSV", "XML", "PITCLIPSE_MUTATIONS", "PITCLIPSE_SUMMARY"};
        assertThat(factoriesName, hasItems(expectedFactoriesName));
    }

    private <T> Matcher<T> serializable() {
        return new TypeSafeMatcher<T>() {
            @Override
            protected boolean matchesSafely(T candidate) {
                try {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    new ObjectOutputStream(byteStream).writeObject(candidate);
                    new ObjectInputStream(new ByteArrayInputStream(byteStream.toByteArray())).readObject();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is serializable");
            }
        };
    }

    private Matcher<File> aFileThatExists() {
        return new TypeSafeMatcher<File>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("file exists");
            }

            @Override
            protected boolean matchesSafely(File file) {
                return null != file && file.exists();
            }
        };
    }

    private static PitOptions options() throws IOException {
        File srcDir = new File(System.getProperty("user.dir") + File.separator + "src");
        return PitOptions.builder().withSourceDirectory(srcDir)
                                   .withClassUnderTest(TEST_CLASS)
                                   .withClassesToMutate(CLASS_PATH)
                                   .withClassPath(classPathWithPitestAndJUnit())
                                   .build();
    }
    
    private static List<String> classPathWithPitestAndJUnit() throws IOException {
        return asList(
            // TODO [Refactor] Pitest's classpath should be managed by the org.pitest bundle
            getBundleFile(Platform.getBundle("org.pitest")).getCanonicalPath(),
            getBundleFile(Platform.getBundle("org.pitest.pitclipse.runner")).getCanonicalPath(),
            getBundleFile(Platform.getBundle("org.pitest.pitclipse.runner")).getCanonicalPath() + File.separator + "bin",
            getBundleFile(Platform.getBundle("org.pitest")).getCanonicalPath() + File.separator + "pitest-1.4.6.jar",
            getBundleFile(Platform.getBundle("org.pitest")).getCanonicalPath() + File.separator + "pitest-entry-1.4.6.jar",
            getBundleFile(Platform.getBundle("org.pitest")).getCanonicalPath() + File.separator + "pitest-command-line-1.4.6.jar",
            getBundleFile(Platform.getBundle("org.pitest")).getCanonicalPath() + File.separator + "pitest-html-report-1.4.6.jar",
            getBundleFile(Platform.getBundle("com.google.guava")).getCanonicalPath(),
            // Add .class files to mutate
            new File("bin").getAbsolutePath(),
            // Add JUnit dependency
            new File("lib/junit.jar").getAbsolutePath()
        );
    }
    
}
