package com.carlosjimz87.pdfrenderer
//
//val Any.TAG: String
//    get() {
//        val tagSimpleName = javaClass.simpleName
//        val tagName = javaClass.name
//        return when {
//            tagSimpleName.isNotBlank() -> {
//                if (tagSimpleName.length > 23) {
//                    tagSimpleName.takeLast(23)
//                } else {
//                    tagSimpleName
//                }
//            }
//            tagName.isNotBlank() -> {
//                if (tagName.length > 23) {
//                    tagName.takeLast(23)
//                } else {
//                    tagName
//                }
//            }
//            else -> {
//                "TAG unknow"
//            }
//        }
//    }

val Any.TAG:String
    get() = "ZZZ"