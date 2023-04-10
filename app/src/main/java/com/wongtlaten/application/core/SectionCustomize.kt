package com.wongtlaten.application.core

class SectionCustomize(private var sectionName: String,
                       private var sectionItems: ArrayList<CustomizeProducts>
) {

    //    fun SectionCustomize(sectionName: String?, sectionItems: ArrayList<CustomizeProducts>) {
//        this.sectionName = sectionName
//        this.sectionItems = sectionItems
//    }

    fun getSectionName(): String {
        return sectionName
    }

    fun setSectionName(sectionName: String) {
        this.sectionName = sectionName
    }

    fun getSectionItems(): ArrayList<CustomizeProducts> {
        return sectionItems
    }

    fun setSectionItems(sectionItems: ArrayList<CustomizeProducts>) {
        this.sectionItems = sectionItems
    }

}