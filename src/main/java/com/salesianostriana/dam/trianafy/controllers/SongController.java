package com.salesianostriana.dam.trianafy.controllers;

import com.salesianostriana.dam.trianafy.dto.songDto.CreateSongDto;
import com.salesianostriana.dam.trianafy.dto.songDto.GetSongDto;
import com.salesianostriana.dam.trianafy.dto.songDto.SongDtoConverter;
import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.repos.ArtistRepository;
import com.salesianostriana.dam.trianafy.repos.SongRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Canciones", description = "Controlador de Canciones")
public class SongController {
    private final SongRepository repo;
    private final SongDtoConverter dtoConverter;
    private final ArtistRepository artistRepo;


    @Operation(summary = "Obtiene una lista de todas las canciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se han encontrado canciones",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GetSongDto.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            [
                                                {
                                                    "id": 1,
                                                    "title": "The song",
                                                    "album": "The album",
                                                    "year": "2000",
                                                    "artist": "Artist name"
                                                },
                                                {
                                                    "id": 2,
                                                    "title": "The song 2",
                                                    "album": "The album 2",
                                                    "year": "2000",
                                                    "artist": "Artist name 2"
                                                }
                                            ]                                  
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ninguna cancion",
                    content = @Content),
    })
    @GetMapping("/song/")
    public ResponseEntity <List<GetSongDto>> getAllSongs(){
        List<Song> data = repo.findAll();
        if(data.isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            List<GetSongDto> result = data.stream()
                    .map(GetSongDto::of)
                    .collect(Collectors.toList());
            return ResponseEntity
                    .ok()
                    .body(result);
        }
    }



    @Operation(summary = "Obtiene una cancion por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha encontrado la cancion",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class),
                            examples = {@ExampleObject(
                                    value = """
                                                {
                                                    "id": 7,
                                                    "title": "The title",
                                                    "album": "The album",
                                                    "year": "2000",
                                                    "artist": {
                                                        "id": 2,
                                                        "name": "The name"
                                                    }
                                                }           
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ninguna cancion",
                    content = @Content),
    })
    @GetMapping("/song/{id}")
    public ResponseEntity<Song> getSong(@Parameter(description = " ID de la cancion a consultar")@PathVariable Long id){
        return ResponseEntity
                .of(repo.findById(id));
    }

    @Operation(summary = "Añade una nueva cancion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Se ha creado satisfactoriamente la cancion",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetSongDto.class),
                            examples = {@ExampleObject(
                                    value = """
                                                {    "id": 1,
                                                     "title": "The song",
                                                     "album": "The album",
                                                     "year": "2022",
                                                     "artist": "Artist name"}                                                                             
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400",
                    description = "Ha habido un error en los datos recibidos",
                    content = @Content),
    })
    @PostMapping("/song/")
    public ResponseEntity<GetSongDto> addSong(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = " Objeto Dto necesario para la creacion de la cancion") @RequestBody CreateSongDto dto){
        if(dto.getArtistId() ==null || dto.getTitle()== null || dto.getAlbum() == null || dto.getYear()== null){
            return ResponseEntity.badRequest().build();
        }

        Song newSong = dtoConverter.createSongDtoToSong(dto);

        Artist artist = artistRepo.findById(dto.getArtistId()).orElse(null);

        newSong.setArtist(artist);
        repo.save(newSong);

        GetSongDto getSongDto = dtoConverter.songToGetSongDto(newSong);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(getSongDto);
    }


    @Operation(summary = "Modifica la cancion con ID especificado por la cancion recibido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha realizado correctamente la edicion",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetSongDto.class),
                            examples = {@ExampleObject(
                                    value = """
                                                {    "id": 4,
                                                     "title": "The title",
                                                     "album": "The album",
                                                     "year": "2022",
                                                     "artist": "Artist name"}                                                                             
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400",
                    description = "Ha habido un error en los datos recibidos",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ninguna cancion",
                    content = @Content),

    })
    @PutMapping("/song/{id}")
    public ResponseEntity<GetSongDto> editSong(@Parameter(description = " ID de la cancion a editar")@PathVariable Long id, @io.swagger.v3.oas.annotations.parameters.RequestBody(description = " Objeto Dto necesario para la edicion de la cancion") @RequestBody CreateSongDto dto){
        if(dto.getArtistId() ==null || dto.getTitle()== null || dto.getAlbum() == null || dto.getYear()== null){
            return ResponseEntity.badRequest().build();
        }
        Artist artist = artistRepo.findById(dto.getArtistId()).orElse(null);

        Song s = repo.findById(id).map(old ->{
            old.setTitle(dto.getTitle());
            old.setAlbum(dto.getAlbum());
            old.setYear(dto.getYear());
            old.setArtist(artist);
            return repo.save(old);})
                .orElse(null);

        if(s ==null){
         return ResponseEntity.notFound().build();
        }

        GetSongDto getSongDto = dtoConverter.songToGetSongDto(s);

        return ResponseEntity.ok(getSongDto);
    }

    @Operation(summary = "Elimina una cancion con ID especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Se ha realizado correctamente la eliminacion, y por tanto no hay contenido",
                    content = @Content
            )
    })
    @DeleteMapping("/song/{id}")
    public ResponseEntity<?> deleteSong(@Parameter(description = " ID de la cancion a consultar")@PathVariable Long id){
        if(repo.existsById(id)) {
            repo.deleteById(id);
        }
        return ResponseEntity.noContent().build();
    }
}
