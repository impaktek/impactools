package com.impactech.impactools.impaktor.model

interface Error

class ApiException(val error: String) : Error