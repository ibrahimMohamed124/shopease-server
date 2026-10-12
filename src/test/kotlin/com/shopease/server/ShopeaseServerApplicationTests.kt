package com.shopease.server

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class ShopeaseServerApplicationTests {

	@Test
	fun applicationUsesDocumentedBasePackage() {
		assertEquals("com.shopease.server", ShopeaseServerApplication::class.java.packageName)
	}

}
