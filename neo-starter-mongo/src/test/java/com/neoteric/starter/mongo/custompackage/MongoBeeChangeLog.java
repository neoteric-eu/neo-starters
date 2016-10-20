package com.neoteric.starter.mongo.custompackage;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.neoteric.starter.mongo.MongoBeeAutoConfigurationTest;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

@ChangeLog(order = "002")
public class MongoBeeChangeLog {

    @ChangeSet(order="001", id="changeFromCustomPackage", author = "someAuthor")
    public void anotherChange(Jongo jongo) {
        MongoCollection mycollection = jongo.getCollection(MongoBeeAutoConfigurationTest.MY_COLLECTION);
        mycollection.insert("{testCustomPackage : 1}");
    }
}