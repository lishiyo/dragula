package com.lishiyo.kotlin.features.casualq.data

import com.google.gson.annotations.SerializedName

/**
 * Created by connieli on 6/9/17.
 */

// Response for comments on a post. Returns array of Listing.
// /r/casualconversation/comments/6djhr4?limit=500&showmore=true&sort=top
//class PostCommentsResponse(
//        val listings: List<Listing> = listOf()
//)

data class Listing(
        @SerializedName("kind") val kind: String,
        @SerializedName("data") val data: ListingData
)

class ListingData(
        @SerializedName("modhash") val modhash: String,
        @SerializedName("children") val children: Array<Child>,
        @SerializedName("after") val after: String?,
        @SerializedName("before") val before: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ListingData

        if (modhash != other.modhash) return false
//        if (!Arrays.equals(children, other.children)) return false
        return (children contentEquals other.children)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

// object in children array
data class Child(
        @SerializedName("kind") val kind: String,
        @SerializedName("data") val comment: Comment
)

class Comment(
        @SerializedName("author") val author: String,
        @SerializedName("parent_id") val parent_id: String,
        @SerializedName("body") val body: String,
        @SerializedName("replies") val replies: Listing, // TODO can be ""
        @SerializedName("ups") val ups: Int,
        @SerializedName("downs") val downs: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Comment

        if (author != other.author) return false
        if (parent_id != other.parent_id) return false
        if (body != other.body) return false
        if (ups != other.ups) return false
        if (downs != other.downs) return false
        return (replies == other.replies)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}