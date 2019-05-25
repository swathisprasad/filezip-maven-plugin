/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techshard;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Main class for creating file zip.
 *
 * @author Swathi Prasad
 *
 */
@Mojo (name = "zip", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class FileZipMojo extends AbstractMojo {

    @Parameter (required = true)
    private File input;

    @Parameter
    private File zipName;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    private static final String FILE_EXTENSION= ".zip";

    public void execute() throws MojoExecutionException {
        getLog().info("Zipping files in \"" + input.getPath() + "\".");

        final String outputFileName = zipName != null ? zipName + FILE_EXTENSION : project.getName() + "-" + project.getVersion() + FILE_EXTENSION;

        try {
            final File dir = new File(input.getPath());
            final File[] files = dir.listFiles();

            if (files != null) {
                final FileOutputStream fos = new FileOutputStream(outputFileName);
                final ZipOutputStream zipOut = new ZipOutputStream(fos);
                for (final File file : files) {
                    final String fileName = file.getName();

                    getLog().info("Zipping File  " + fileName);

                    final FileInputStream fis = new FileInputStream(file);
                    final ZipEntry zipEntry = new ZipEntry(file.getName());

                    if (file.isDirectory()){
                        if (fileName.endsWith("/")){
                            zipOut.putNextEntry(new ZipEntry(fileName));
                        } else {
                            zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                        }
                    } else {
                        zipOut.putNextEntry(zipEntry);
                    }

                    final byte[] bytes = new byte[2048];
                    int length;
                    while((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    fis.close();
                }
                zipOut.close();
                fos.close();
            }
        } catch(final FileNotFoundException e) {
            throw new MojoExecutionException("No file found", e);
        } catch(final IOException e) {
            throw new MojoExecutionException("Exception reading files", e);
        }
    }
}
