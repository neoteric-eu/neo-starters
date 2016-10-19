package com.neoteric.starter.mongo;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;

public abstract class AbstractMongoDBTest {
    /**
     * please store Starter or RuntimeConfig in a static final field
     * if you want to use artifact store caching (or else disable caching)
     */
    private static final MongodStarter starter = MongodStarter.getDefaultInstance();

    private MongodExecutable _mongodExe;
    private MongodProcess _mongod;
    private MongoClient _mongo;

    protected static int freePort;

    @BeforeClass
    public static void setPort() throws IOException {
        freePort = Network.getFreeServerPort();
    }

    @Before
    public void setUp() throws Exception {

        _mongodExe = starter.prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(freePort, Network.localhostIsIPv6()))
                .build());
        _mongod = _mongodExe.start();
        _mongo = new MongoClient("localhost", freePort);
    }

    @After
    public void tearDown() throws Exception {
        _mongod.stop();
        _mongodExe.stop();
    }

    public Mongo getMongo() {
        return _mongo;
    }

}
