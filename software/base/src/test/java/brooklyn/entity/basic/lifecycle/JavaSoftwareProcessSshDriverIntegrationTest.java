package brooklyn.entity.basic.lifecycle;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import brooklyn.entity.basic.ApplicationBuilder;
import brooklyn.entity.basic.BrooklynConfigKeys;
import brooklyn.entity.basic.Entities;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.location.MachineProvisioningLocation;
import brooklyn.location.basic.LocalhostMachineProvisioningLocation;
import brooklyn.management.internal.LocalManagementContext;
import brooklyn.test.Asserts;
import brooklyn.test.entity.TestApplication;
import brooklyn.util.ResourceUtils;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.os.Os;
import brooklyn.util.text.Strings;

import com.google.common.collect.ImmutableList;

public class JavaSoftwareProcessSshDriverIntegrationTest {

    private static final long TIMEOUT_MS = 10*1000;

    private static final Logger log = LoggerFactory.getLogger(JavaSoftwareProcessSshDriverIntegrationTest.class);
    
    private MachineProvisioningLocation localhost;
    private TestApplication app;

    @BeforeMethod(alwaysRun=true)
    public void setup() {
        localhost = new LocalhostMachineProvisioningLocation(MutableMap.of("name", "localhost"));
        app = ApplicationBuilder.newManagedApp(TestApplication.class);
    }

    @AfterMethod(alwaysRun=true)
    public void shutdown() {
        if (app != null) Entities.destroyAll(app.getManagementContext());
    }

    @Test(groups = "Integration")
    public void testJavaStartStopSshDriverStartsAndStopsApp() {
        final MyEntity entity = app.createAndManageChild(EntitySpec.create(MyEntity.class));
        app.start(ImmutableList.of(localhost));
        Asserts.succeedsEventually(MutableMap.of("timeout", TIMEOUT_MS), new Runnable() {
            public void run() {
                assertTrue(entity.getAttribute(SoftwareProcess.SERVICE_UP));
            }});
        
        entity.stop();
        assertFalse(entity.getAttribute(SoftwareProcess.SERVICE_UP));
    }

    @Test(groups = "Integration")
    public void testStartsInMgmtSpecifiedDirectory() {
        String dir = Os.mergePathsUnix(Os.tmp(), "/brooklyn-test-"+Strings.makeRandomId(4));
        shutdown();
        LocalManagementContext mgmt = new LocalManagementContext();
        mgmt.getBrooklynProperties().put(BrooklynConfigKeys.BROOKLYN_DATA_DIR, dir);
        app = ApplicationBuilder.newManagedApp(TestApplication.class, mgmt);
        
        doTestSpecifiedDirectory(dir, dir);
        Os.tryDeleteDirectory(dir);
    }
    
    @Test(groups = "Integration")
    public void testStartsInAppSpecifiedDirectoryUnderHome() {
        String dir = Os.mergePathsUnix("~/.brooklyn-test-"+Strings.makeRandomId(4));
        try {
            app.setConfig(BrooklynConfigKeys.BROOKLYN_DATA_DIR, dir);
            doTestSpecifiedDirectory(dir, dir);
        } finally {
            Os.tryDeleteDirectory(dir);
        }
    }
    
    @Test(groups = "Integration")
    public void testStartsInDifferentRunAndInstallSpecifiedDirectories() {
        String dir1 = Os.mergePathsUnix(Os.tmp(), "/brooklyn-test-"+Strings.makeRandomId(4));
        String dir2 = Os.mergePathsUnix(Os.tmp(), "/brooklyn-test-"+Strings.makeRandomId(4));
        app.setConfig(BrooklynConfigKeys.INSTALL_DIR, dir1);
        app.setConfig(BrooklynConfigKeys.RUN_DIR, dir2);
        doTestSpecifiedDirectory(dir1, dir2);
        Os.tryDeleteDirectory(dir1);
        Os.tryDeleteDirectory(dir2);
    }
    
    @Test(groups = "Integration")
    public void testStartsInLegacySpecifiedDirectory() {
        String dir1 = Os.mergePathsUnix(Os.tmp(), "/brooklyn-test-"+Strings.makeRandomId(4));
        String dir2 = Os.mergePathsUnix(Os.tmp(), "/brooklyn-test-"+Strings.makeRandomId(4));
        shutdown();
        LocalManagementContext mgmt = new LocalManagementContext();
        mgmt.getBrooklynProperties().put("brooklyn.dirs.install", dir1);
        mgmt.getBrooklynProperties().put("brooklyn.dirs.run", dir2);
        app = ApplicationBuilder.newManagedApp(TestApplication.class, mgmt);
        
        app.setConfig(BrooklynConfigKeys.RUN_DIR, dir2);
        doTestSpecifiedDirectory(dir1, dir2);
        Os.tryDeleteDirectory(dir1);
        Os.tryDeleteDirectory(dir2);
    }
    
    protected void doTestSpecifiedDirectory(final String installDirPrefix, final String runDirPrefix) {
        final MyEntity entity = app.createAndManageChild(EntitySpec.create(MyEntity.class));
        app.start(ImmutableList.of(localhost));
        Asserts.succeedsEventually(MutableMap.of("timeout", TIMEOUT_MS), new Runnable() {
            public void run() {
                assertTrue(entity.getAttribute(SoftwareProcess.SERVICE_UP));
                
                String installDir = entity.getAttribute(SoftwareProcess.INSTALL_DIR);
                Assert.assertNotNull(installDir);
                
                String runDir = entity.getAttribute(SoftwareProcess.RUN_DIR);
                Assert.assertNotNull(runDir);
            }});
        
        String installDir = entity.getAttribute(SoftwareProcess.INSTALL_DIR);
        String runDir = entity.getAttribute(SoftwareProcess.RUN_DIR);
        log.info("dirs for "+app+" are: install="+installDir+", run="+runDir);
        assertTrue(installDir.startsWith(ResourceUtils.tidyFilePath(installDirPrefix)), "INSTALL_DIR is "+installDir+", does not start with expected prefix "+installDirPrefix);
        assertTrue(runDir.startsWith(ResourceUtils.tidyFilePath(runDirPrefix)), "RUN_DIR is "+runDir+", does not start with expected prefix "+runDirPrefix);
        
        entity.stop();
        assertFalse(entity.getAttribute(SoftwareProcess.SERVICE_UP));
    }

}