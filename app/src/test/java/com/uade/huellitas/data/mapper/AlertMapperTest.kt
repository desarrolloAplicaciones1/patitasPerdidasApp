package com.uade.huellitas.data.mapper

import com.uade.huellitas.makeAlert
import com.uade.huellitas.makeAlertEntity
import com.uade.huellitas.domain.model.AlertStatus
import com.uade.huellitas.domain.model.AlertType
import com.uade.huellitas.domain.model.PetType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertMapperTest {

    // ── entity → domain ──────────────────────────────────────────────────────

    @Test
    fun `toDomain maps enums correctly`() {
        val domain = makeAlertEntity(type = "LOST", status = "ACTIVE", petType = "DOG").toDomain()
        assertEquals(AlertType.LOST, domain.type)
        assertEquals(AlertStatus.ACTIVE, domain.status)
        assertEquals(PetType.DOG, domain.petType)
    }

    @Test
    fun `toDomain maps FOUND type`() {
        assertEquals(AlertType.FOUND, makeAlertEntity(type = "FOUND").toDomain().type)
    }

    @Test
    fun `toDomain maps RESOLVED status`() {
        assertEquals(AlertStatus.RESOLVED, makeAlertEntity(status = "RESOLVED").toDomain().status)
    }

    @Test
    fun `toDomain maps CAT and OTHER pet types`() {
        assertEquals(PetType.CAT,   makeAlertEntity(petType = "CAT").toDomain().petType)
        assertEquals(PetType.OTHER, makeAlertEntity(petType = "OTHER").toDomain().petType)
    }

    @Test
    fun `toDomain splits photoUrlsJson into list`() {
        val domain = makeAlertEntity(photoUrlsJson = "url1|url2|url3").toDomain()
        assertEquals(listOf("url1", "url2", "url3"), domain.photoUrls)
    }

    @Test
    fun `toDomain with single photo returns single-element list`() {
        val domain = makeAlertEntity(photoUrlsJson = "url1").toDomain()
        assertEquals(listOf("url1"), domain.photoUrls)
    }

    @Test
    fun `toDomain with empty photoUrlsJson returns empty list`() {
        val domain = makeAlertEntity(photoUrlsJson = "").toDomain()
        assertTrue(domain.photoUrls.isEmpty())
    }

    @Test
    fun `toDomain maps location fields`() {
        val domain = makeAlertEntity().toDomain()
        assertEquals(-34.6037, domain.location.latitude,  0.0001)
        assertEquals(-58.3816, domain.location.longitude, 0.0001)
        assertEquals("Buenos Aires", domain.location.address)
    }

    @Test
    fun `toDomain maps null optional fields`() {
        val entity = makeAlertEntity().copy(petId = null, breed = null, color = null, address = null)
        val domain = entity.toDomain()
        assertNull(domain.petId)
        assertNull(domain.breed)
        assertNull(domain.color)
        assertNull(domain.location.address)
    }

    // ── domain → entity ──────────────────────────────────────────────────────

    @Test
    fun `toEntity serializes enum names`() {
        val entity = makeAlert(type = AlertType.FOUND, status = AlertStatus.RESOLVED, petType = PetType.CAT).toEntity()
        assertEquals("FOUND",    entity.type)
        assertEquals("RESOLVED", entity.status)
        assertEquals("CAT",      entity.petType)
    }

    @Test
    fun `toEntity joins photoUrls with pipe separator`() {
        val entity = makeAlert(photoUrls = listOf("a", "b", "c")).toEntity()
        assertEquals("a|b|c", entity.photoUrlsJson)
    }

    @Test
    fun `toEntity with empty photoUrls stores empty string`() {
        val entity = makeAlert(photoUrls = emptyList()).toEntity()
        assertEquals("", entity.photoUrlsJson)
    }

    @Test
    fun `toEntity pendingSync defaults to false`() {
        assertFalse(makeAlert().toEntity().pendingSync)
    }

    @Test
    fun `toEntity pendingSync can be set to true`() {
        assertTrue(makeAlert().toEntity(pendingSync = true).pendingSync)
    }

    // ── round-trip ───────────────────────────────────────────────────────────

    @Test
    fun `entity round-trip preserves id, type, and photoUrls`() {
        val original = makeAlertEntity(id = "x-99", type = "FOUND", photoUrlsJson = "p1|p2")
        val result   = original.toDomain().toEntity()
        assertEquals(original.id,           result.id)
        assertEquals(original.type,         result.type)
        assertEquals(original.photoUrlsJson, result.photoUrlsJson)
    }

    @Test
    fun `domain round-trip preserves location coordinates`() {
        val original = makeAlert()
        val result   = original.toEntity().toDomain()
        assertEquals(original.location.latitude,  result.location.latitude,  0.0001)
        assertEquals(original.location.longitude, result.location.longitude, 0.0001)
    }
}
