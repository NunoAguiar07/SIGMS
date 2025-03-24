package db

import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite


@Suite
@SelectClasses(
    value = []
)
@IncludeEngines("junit-jupiter")

class DatabaseTestSuite