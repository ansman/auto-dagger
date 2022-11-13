package se.ansman.deager

import org.junit.jupiter.api.Disabled

@Disabled("KSP is not ready yet")
class KspTest : BaseTest("ksp", ::KspCompilation)