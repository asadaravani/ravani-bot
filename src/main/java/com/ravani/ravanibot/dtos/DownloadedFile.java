package com.ravani.ravanibot.dtos;

import lombok.Builder;

@Builder
public record DownloadedFile (
        byte[] bytes, String contentType
){}
