/*
 * Copyright 2016-2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.galleon.featurepack.dependency.merge.test;

import org.jboss.galleon.ArtifactCoords;
import org.jboss.galleon.ProvisioningDescriptionException;
import org.jboss.galleon.ProvisioningException;
import org.jboss.galleon.config.FeaturePackConfig;
import org.jboss.galleon.config.ProvisioningConfig;
import org.jboss.galleon.repomanager.FeaturePackRepositoryManager;
import org.jboss.galleon.state.ProvisionedFeaturePack;
import org.jboss.galleon.state.ProvisionedState;
import org.jboss.galleon.test.PmProvisionConfigTestBase;
import org.jboss.galleon.test.util.fs.state.DirState;

/**
 *
 * @author Alexey Loubyansky
 */
public class MergedSimpleExcludesTestCase extends PmProvisionConfigTestBase {

    @Override
    protected void setupRepo(FeaturePackRepositoryManager repoManager) throws ProvisioningDescriptionException {
        repoManager.installer()
            .newFeaturePack(ArtifactCoords.newGav("org.jboss.pm.test", "fp1", "1.0.0.Alpha"))
                .addDependency(FeaturePackConfig.forGav(ArtifactCoords.newGav("org.jboss.pm.test", "fp2", "2.0.0.Final")))
                .addDependency(FeaturePackConfig.forGav(ArtifactCoords.newGav("org.jboss.pm.test", "fp3", "2.0.0.Final")))
                .newPackage("p1", true)
                    .writeContent("fp1/p1.txt", "p1")
                    .getFeaturePack()
                .getInstaller()
            .newFeaturePack(ArtifactCoords.newGav("org.jboss.pm.test", "fp2", "2.0.0.Final"))
                .addDependency(FeaturePackConfig
                        .builder(ArtifactCoords.newGav("org.jboss.pm.test", "fp4", "2.0.0.Final"))
                        .excludePackage("p1")
                        .excludePackage("p2")
                        .build())
                .newPackage("p1", true)
                    .writeContent("fp2/p1.txt", "p1")
                    .getFeaturePack()
                .getInstaller()
            .newFeaturePack(ArtifactCoords.newGav("org.jboss.pm.test", "fp3", "2.0.0.Final"))
                .addDependency(FeaturePackConfig
                        .builder(ArtifactCoords.newGav("org.jboss.pm.test", "fp4", "2.0.0.Final"))
                        .excludePackage("p1")
                        .excludePackage("p3")
                        .build())
                .newPackage("p1", true)
                    .writeContent("fp3/p1.txt", "p1")
                    .getFeaturePack()
                .getInstaller()
            .newFeaturePack(ArtifactCoords.newGav("org.jboss.pm.test", "fp4", "2.0.0.Final"))
                .newPackage("p1", true)
                    .writeContent("fp4/p1.txt", "p1")
                    .getFeaturePack()
                .newPackage("p2", true)
                    .writeContent("fp4/p2.txt", "p2")
                    .getFeaturePack()
                .newPackage("p3", true)
                    .writeContent("fp4/p3.txt", "p3")
                    .getFeaturePack()
                .getInstaller()
            .install();
    }

    @Override
    protected ProvisioningConfig provisioningConfig() throws ProvisioningDescriptionException {
        return ProvisioningConfig.builder()
                .addFeaturePackDep(
                        FeaturePackConfig.forGav(
                                ArtifactCoords.newGav("org.jboss.pm.test", "fp1", "1.0.0.Alpha")))
                .build();
    }

    @Override
    protected ProvisionedState provisionedState() throws ProvisioningException {
        return ProvisionedState.builder()
                .addFeaturePack(ProvisionedFeaturePack.builder(ArtifactCoords.newGav("org.jboss.pm.test", "fp1", "1.0.0.Alpha"))
                        .addPackage("p1")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(ArtifactCoords.newGav("org.jboss.pm.test", "fp2", "2.0.0.Final"))
                        .addPackage("p1")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(ArtifactCoords.newGav("org.jboss.pm.test", "fp3", "2.0.0.Final"))
                        .addPackage("p1")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(ArtifactCoords.newGav("org.jboss.pm.test", "fp4", "2.0.0.Final"))
                        .addPackage("p2")
                        .addPackage("p3")
                        .build())
                .build();
    }

    @Override
    protected DirState provisionedHomeDir() {
        return newDirBuilder()
                .addFile("fp1/p1.txt", "p1")
                .addFile("fp2/p1.txt", "p1")
                .addFile("fp3/p1.txt", "p1")
                .addFile("fp4/p2.txt", "p2")
                .addFile("fp4/p3.txt", "p3")
                .build();
    }
}
