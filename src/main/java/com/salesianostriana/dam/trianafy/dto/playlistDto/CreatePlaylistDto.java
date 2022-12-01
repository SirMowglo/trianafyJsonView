package com.salesianostriana.dam.trianafy.dto.playlistDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class CreatePlaylistDto {
    private Long id;
    private String name;
    private String description;
}