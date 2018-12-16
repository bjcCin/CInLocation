package com.wehack.cinlocation.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Representa um lembrete cadastrado pelo usuário
 *
 * @param title título do lembrete
 * @param text texto adicional do lembrete
 * @param lat latitude da localizacao escolhida pelo usuario
 * @param long longitude da localizacao escolhida pelo usuario
 * @param beginDate data a partir da qual o lembrete será valido
 * @param endDate data de expiração
 * @param completed indica se o lembrete ainda é válido
 * @param image imagem adicionada pelo usuário
 */
@Entity
data class Reminder(
        @PrimaryKey(autoGenerate = true) var id: Long? = null,
        var title: String = "",
        var text: String = "",
        var lat: Double? = 0.0,
        var lon: Double? = 0.0 ,
        var beginDate: Date? = null,
        var endDate: Date? = null,
        var completed: Boolean = false,
        var image: String? = null,
        var placeName: String? = "")