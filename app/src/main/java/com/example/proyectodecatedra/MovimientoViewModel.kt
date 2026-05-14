package com.example.proyectodecatedra

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MovimientoViewModel : ViewModel() {

    // Tipo de movimiento
    private val _tipoMovimiento = MutableLiveData("GASTO")
    val tipoMovimiento: LiveData<String> = _tipoMovimiento

    // Campos del formulario
    private val _titulo = MutableLiveData("")
    val titulo: LiveData<String> = _titulo

    private val _descripcion = MutableLiveData("")
    val descripcion: LiveData<String> = _descripcion

    private val _monto = MutableLiveData("")
    val monto: LiveData<String> = _monto

    private val _categoria = MutableLiveData("Alimentación")
    val categoria: LiveData<String> = _categoria

    private val _metodoPago = MutableLiveData("Efectivo")
    val metodoPago: LiveData<String> = _metodoPago

    private val _dia = MutableLiveData("")
    val dia: LiveData<String> = _dia

    private val _mes = MutableLiveData("")
    val mes: LiveData<String> = _mes

    private val _anio = MutableLiveData("")
    val anio: LiveData<String> = _anio

    private val _etiquetas = MutableLiveData<MutableList<String>>(mutableListOf())
    val etiquetas: LiveData<MutableList<String>> = _etiquetas

    // Categorías
    val categorias = listOf(
        "Alimentación",
        "Transporte",
        "Salud",
        "Educación",
        "Entretenimiento",
        "Servicios",
        "Compras",
        "Salario",
        "Ahorro",
        "Otros"
    )

    // Métodos de pago
    val metodosPago = listOf(
        "Efectivo",
        "Tarjeta",
        "Transferencia",
        "Crédito",
        "Débito"
    )

    // Setters
    fun setTipoMovimiento(valor: String) {
        _tipoMovimiento.value = valor
    }

    fun setTitulo(valor: String) {
        _titulo.value = valor
    }

    fun setDescripcion(valor: String) {
        _descripcion.value = valor
    }

    fun setMonto(valor: String) {
        _monto.value = valor
    }

    fun setCategoria(valor: String) {
        _categoria.value = valor
    }

    fun setMetodoPago(valor: String) {
        _metodoPago.value = valor
    }

    fun setDia(valor: String) {
        _dia.value = valor
    }

    fun setMes(valor: String) {
        _mes.value = valor
    }

    fun setAnio(valor: String) {
        _anio.value = valor
    }

    // Etiquetas
    fun agregarEtiqueta(etiqueta: String) {

        if (etiqueta.isBlank()) return

        val listaActual = _etiquetas.value ?: mutableListOf()

        if (!listaActual.contains(etiqueta)) {
            listaActual.add(etiqueta)
            _etiquetas.value = listaActual
        }
    }

    fun eliminarEtiqueta(etiqueta: String) {

        val listaActual = _etiquetas.value ?: mutableListOf()

        listaActual.remove(etiqueta)

        _etiquetas.value = listaActual
    }

    // Limpiar formulario
    fun limpiarFormulario() {

        _tipoMovimiento.value = "GASTO"

        _titulo.value = ""
        _descripcion.value = ""
        _monto.value = ""

        _categoria.value = "Alimentación"
        _metodoPago.value = "Efectivo"

        _dia.value = ""
        _mes.value = ""
        _anio.value = ""

        _etiquetas.value = mutableListOf()
    }
}