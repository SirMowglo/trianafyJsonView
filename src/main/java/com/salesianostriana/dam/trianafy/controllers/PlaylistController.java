package com.salesianostriana.dam.trianafy.controllers;

import com.salesianostriana.dam.trianafy.dto.playlistDto.CreatePlaylistDto;
import com.salesianostriana.dam.trianafy.dto.playlistDto.GetPlaylistDto;
import com.salesianostriana.dam.trianafy.dto.playlistDto.PlaylistDtoConverter;
import com.salesianostriana.dam.trianafy.dto.songDto.GetSongDto;
import com.salesianostriana.dam.trianafy.model.Playlist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.repos.PlaylistRepository;
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
@Tag(name = "Playlist", description = "Controlador de Playlists")
public class PlaylistController {
    private final PlaylistRepository repo;
    private final SongRepository songRepo;
    private final PlaylistDtoConverter dtoConverter;


    @Operation(summary = "Obtiene una lista de todas las Playlists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se han encontrado playlists",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GetPlaylistDto.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            [
                                                    {
                                                        "id": 12,
                                                        "name": "The name",
                                                        "numberOfSongs": 4
                                                    },
                                                    {
                                                        "id": 13,
                                                        "name": "The name 2",
                                                        "numberOfSongs": 5
                                                    }
                                            ]                                  
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ninguna playlist",
                    content = @Content),
    })
    @GetMapping("/list/")
    public ResponseEntity<List<GetPlaylistDto>> getAllPlaylists(){
        List<Playlist> data = repo.findAll();
        if(data.isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            List<GetPlaylistDto> result = data.stream()
                    .map(GetPlaylistDto::of)
                    .collect(Collectors.toList());

            return ResponseEntity
                    .ok()
                    .body(result);
        }
    }

    @Operation(summary = "Obtiene una playlist por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha encontrado la playlist",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Playlist.class),
                            examples = {@ExampleObject(
                                    value = """
                                                {
                                                    "id": 12,
                                                    "name": "The name",
                                                    "description": "The description",
                                                    "songs": [
                                                        {
                                                            "id": 9,
                                                            "title": "Enter Sandman",
                                                            "album": "Metallica",
                                                            "year": "1991",
                                                            "artist": {
                                                                "id": 3,
                                                                "name": "Metallica"
                                                            }
                                                        },
                                                        {
                                                            "id": 8,
                                                            "title": "Love Again",
                                                            "album": "Future Nostalgia",
                                                            "year": "2021",
                                                            "artist": {
                                                                "id": 2,
                                                                "name": "Dua Lipa"
                                                            }
                                                        }
                                                    ]
                                                }
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ninguna playlist",
                    content = @Content),
    })
    @GetMapping("/list/{id}")
    public ResponseEntity<Playlist> getPlaylist(@Parameter(description = " ID de la playlist a consultar")@PathVariable Long id){
        return ResponseEntity
                .of(repo.findById(id));
    }


    @Operation(summary = "Añade una nueva playlist sin canciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Se ha creado satisfactoriamente la playlist",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreatePlaylistDto.class),
                            examples = {@ExampleObject(
                                    value = """
                                                    {
                                                        "id": 13,
                                                        "name": "The name",
                                                        "description": "The description"
                                                    }                                                                    
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400",
                    description = "Ha habido un error en los datos recibidos",
                    content = @Content),
    })
    @PostMapping("/list/")
    public ResponseEntity<CreatePlaylistDto> addPlaylist(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = " Objeto Dto necesario para la creacion de la lista") @RequestBody CreatePlaylistDto dto){
        if( dto.getName()== null || dto.getDescription() == null ){
            return ResponseEntity.badRequest().build();
        }
        Playlist nP = dtoConverter.createPlaylistDtoToPlaylist(dto);

        repo.save(nP);

        CreatePlaylistDto dtoPlaylist = new CreatePlaylistDto(
                nP.getId(),
                nP.getName(),
                nP.getDescription()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoPlaylist);
    }


    @Operation(summary = "Modifica la playlist con ID especificado por la playlist recibida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha realizado correctamente la edicion",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetPlaylistDto.class),
                            examples = {@ExampleObject(
                                    value = """
                                            {
                                                "id": 12,
                                                "name": "The name",
                                                "numberOfSongs": 4
                                            }
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400",
                    description = "Ha habido un error en los datos recibidos",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ninguna playlist",
                    content = @Content),

    })
    @PutMapping("/list/{id}")
    public ResponseEntity<GetPlaylistDto> editPlaylist(@Parameter(description = " ID de la playlist a editar")@PathVariable Long id,
                                                       @io.swagger.v3.oas.annotations.parameters.RequestBody(description = " Objeto Dto necesario para edicion de la playlist") @RequestBody CreatePlaylistDto dto){
        if( dto.getName()== null || dto.getDescription() == null ){
            return ResponseEntity.badRequest().build();
        }

        Playlist p = repo.findById(id).map(old ->{
            old.setDescription(dto.getDescription());
            old.setName(dto.getName());
            return repo.save(old);
        }).orElse(null);

        if(p ==null){
            return ResponseEntity.notFound().build();
        }
        GetPlaylistDto getPlaylistDto = dtoConverter.playlistToGetPlaylistDto(p);

        return ResponseEntity.ok(getPlaylistDto);
    }

    @Operation(summary = "Elimina una playlist con ID especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Se ha realizado correctamente la eliminacion, y por tanto no hay contenido",
                    content = @Content
            )
    })
    @DeleteMapping("/list/{id}")
    public ResponseEntity <?> deletePlaylist(@Parameter(description = " ID de la playlist a eliminar")@PathVariable Long id){
        if(repo.existsById(id)){
            repo.deleteById(id);
        }
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Añade una cancion existente a una playlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha realizado correctamente la operacion",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Playlist.class),
                            examples = {@ExampleObject(
                                    value = """
                                            {
                                                "id": 12,
                                                "name": "The name",
                                                "description": "The description",
                                                "songs": [
                                                    {
                                                        "id": 9,
                                                        "title": "Enter Sandman",
                                                        "album": "Metallica",
                                                        "year": "1991",
                                                        "artist": {
                                                            "id": 3,
                                                            "name": "Metallica"
                                                        }
                                                    },
                                                    {
                                                        "id": 8,
                                                        "title": "Love Again",
                                                        "album": "Future Nostalgia",
                                                        "year": "2021",
                                                        "artist": {
                                                            "id": 2,
                                                            "name": "Dua Lipa"
                                                        }
                                                    }
                                                ]
                                            }
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ninguna playlist o cancion",
                    content = @Content),

    })
    @PostMapping("/list/{idList}/song/{idSong}")
    public ResponseEntity<Playlist> addSongToPlaylist(@Parameter(description = " ID de lista a consultar")@PathVariable("idList") Long idList,
                                                      @Parameter(description = " ID de la cancion a añadir")@PathVariable("idSong") Long idSong){
        if(!repo.existsById(idList) || !songRepo.existsById(idSong)){
            return ResponseEntity.notFound().build();
        }else{
            Playlist p = repo.findById(idList).orElse(null);
            Song s = songRepo.findById(idSong).orElse(null);
            p.addSong(s);
            repo.save(p);

            return ResponseEntity.ok(p);
        }
    }


    @Operation(summary = "Obtiene todas las canciones de una playlist existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha encontrado la lista de canciones de la playlist",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Playlist.class),
                            examples = {@ExampleObject(
                                    value = """
                                            {
                                                "id": 12,
                                                "name": "The name",
                                                "description": "The description",
                                                "songs": [
                                                    {
                                                        "id": 9,
                                                        "title": "Enter Sandman",
                                                        "album": "Metallica",
                                                        "year": "1991",
                                                        "artist": {
                                                            "id": 3,
                                                            "name": "Metallica"
                                                        }
                                                    },
                                                    {
                                                        "id": 8,
                                                        "title": "Love Again",
                                                        "album": "Future Nostalgia",
                                                        "year": "2021",
                                                        "artist": {
                                                            "id": 2,
                                                            "name": "Dua Lipa"
                                                        }
                                                    }
                                                ]
                                            }
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ninguna lista de canciones",
                    content = @Content),
    })
    @GetMapping("/list/{id}/song")
    public ResponseEntity<Playlist> getAllSongFromPlaylist(@Parameter(description = " ID de la playlist a consultar")@PathVariable Long id){
        if(!repo.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        Playlist p = repo.findById(id).orElse(null);

        return ResponseEntity.ok(p);
    }

    @Operation(summary = "Obtiene una cancion especificada por ID de una playlist existente especificada por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha encontrado la cancion",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class),
                            examples = {@ExampleObject(
                                    value = """
                                            {
                                                "id": 11,
                                                "title": "The title",
                                                "album": "The album",
                                                "year": "2000",
                                                "artist": {
                                                    "id": 1,
                                                    "name": "Artist name"
                                                }
                                            }
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ninguna cancion o playlist",
                    content = @Content),
    })
    @GetMapping("/list/{idList}/song/{idSong}")
    public ResponseEntity<Song> getSongFromPlaylist(@Parameter(description = " ID de la playlist a consultar")@PathVariable("idList") Long idList,
                                                    @Parameter(description = " ID de la cancion a consultar")@PathVariable("idSong") Long idSong){
        if(!repo.existsById(idList) || !songRepo.existsById(idSong)){
            return ResponseEntity.notFound().build();
        }else{
        Playlist p = repo.findById(idList).orElse(null);
        Song s = songRepo.findById(idSong).orElse(null);

        if(p.getSongs().contains(s)){
            return ResponseEntity.ok(s);
        }else{
            return ResponseEntity.notFound().build();
        }
        }
    }

    @Operation(summary = "Elimina una cancion con ID especificado de una playlist con ID especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Se ha realizado correctamente la eliminacion, y por tanto no hay contenido",
                    content = @Content
            ),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ninguna cancion o playlist",
                    content = @Content),
    })
    @DeleteMapping("/list/{idList}/song/{idSong}")
    public ResponseEntity<?> deleteSongFromPlaylist(@Parameter(description = " ID de la playlist a consultar")@PathVariable("idList") Long idList,
                                                    @Parameter(description = " ID de la cancion a eliminar")@PathVariable("idSong") Long idSong){
        if(!repo.existsById(idList) || !songRepo.existsById(idSong)){
            return ResponseEntity.notFound().build();
        }else{
            Playlist p = repo.findById(idList).orElse(null);
            Song s = songRepo.findById(idSong).orElse(null);

            if(p.getSongs().contains(s)){
                for(int i =0; i<p.getSongs().size();i++){
                    if(p.getSongs().get(i) == s){
                        p.deleteSong(p.getSongs().get(i));
                    }
                }
                repo.save(p);
                return ResponseEntity.noContent().build();
            }else{
                return ResponseEntity.notFound().build();
            }
        }
    }
}
