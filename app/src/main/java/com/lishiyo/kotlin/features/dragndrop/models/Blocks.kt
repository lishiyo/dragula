package com.lishiyo.kotlin.features.dragndrop.models

/**
 * Marker interface for data model of the outgoing block.
 *
 * Created by connieli on 7/1/17.
 */
interface Block {
    // com.tumblr.rumblr.model.post.outgoing.blocks.Block.Builder getBuilder()
}

// TextBlock
class MaxOneBlock : Block

// ImageBlock
class MaxThreeBlock : Block