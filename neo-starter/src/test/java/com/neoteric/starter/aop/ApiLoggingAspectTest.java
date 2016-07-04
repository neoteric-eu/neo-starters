package com.neoteric.starter.aop;

import com.google.common.base.CaseFormat;
import humanize.Humanize;
import org.apache.commons.lang.WordUtils;
import org.junit.Test;
import static humanize.Humanize.capitalize;
import static humanize.Humanize.decamelize;

public class ApiLoggingAspectTest {


    @Test
    public void testElo() throws Exception {

        String init = "searchForJobOffers";

        System.out.println(capitalize(decamelize(init)));

    }
}