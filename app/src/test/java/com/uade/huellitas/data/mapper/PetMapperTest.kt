package com.uade.huellitas.data.mapper

import com.uade.huellitas.makePet
import com.uade.huellitas.makePetEntity
import com.uade.huellitas.domain.model.PetType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PetMapperTest {

    // ── entity → domain ──────────────────────────────────────────────────────

    @Test
    fun `toDomain maps all base fields`() {
        val domain = makePetEntity(id = "pet-99", ownerId = "u-1", name = "Rex").toDomain()
        assertEquals("pet-99", domain.id)
        assertEquals("u-1",   domain.ownerId)
        assertEquals("Rex",   domain.name)
    }

    @Test
    fun `toDomain maps pet type enum`() {
        assertEquals(PetType.CAT,   makePetEntity(petType = "CAT").toDomain().petType)
        assertEquals(PetType.DOG,   makePetEntity(petType = "DOG").toDomain().petType)
        assertEquals(PetType.OTHER, makePetEntity(petType = "OTHER").toDomain().petType)
    }

    @Test
    fun `toDomain splits photoUrlsJson into list`() {
        val domain = makePetEntity(photoUrlsJson = "a|b|c").toDomain()
        assertEquals(listOf("a", "b", "c"), domain.photoUrls)
    }

    @Test
    fun `toDomain with empty photoUrlsJson returns empty list`() {
        assertTrue(makePetEntity(photoUrlsJson = "").toDomain().photoUrls.isEmpty())
    }

    @Test
    fun `toDomain maps null optional fields`() {
        val entity = makePetEntity().copy(breed = null, color = null, microchipId = null)
        val domain = entity.toDomain()
        assertNull(domain.breed)
        assertNull(domain.color)
        assertNull(domain.microchipId)
    }

    // ── domain → entity ──────────────────────────────────────────────────────

    @Test
    fun `toEntity serializes petType as enum name`() {
        assertEquals("CAT",   makePet(petType = PetType.CAT).toEntity().petType)
        assertEquals("DOG",   makePet(petType = PetType.DOG).toEntity().petType)
        assertEquals("OTHER", makePet(petType = PetType.OTHER).toEntity().petType)
    }

    @Test
    fun `toEntity joins photoUrls with pipe separator`() {
        val entity = makePet(photoUrls = listOf("x", "y")).toEntity()
        assertEquals("x|y", entity.photoUrlsJson)
    }

    @Test
    fun `toEntity with empty photoUrls stores empty string`() {
        assertEquals("", makePet(photoUrls = emptyList()).toEntity().photoUrlsJson)
    }

    // ── round-trip ───────────────────────────────────────────────────────────

    @Test
    fun `round-trip preserves all fields`() {
        val original = makePetEntity(id = "rt-1", name = "Firulais", photoUrlsJson = "img1|img2")
        val result   = original.toDomain().toEntity()
        assertEquals(original.id,           result.id)
        assertEquals(original.name,         result.name)
        assertEquals(original.petType,      result.petType)
        assertEquals(original.photoUrlsJson, result.photoUrlsJson)
    }
}
