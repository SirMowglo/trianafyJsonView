package com.salesianostriana.dam.trianafy.controllers;

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

@RestController
@RequiredArgsConstructor
@Tag(name = "Artista", description = "Controlador de Artistas")
public class ArtistController {
    private final ArtistRepository repo;
    private final SongRepository repoSong;

    @Operation(summary = "Obtiene una lista de todos los artistas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se han encontrado artistas",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Artist.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            [
                                                {"id": 1, "name": "Joaquin Sabina"},
                                                {"id": 2, "name": "Dua Lipa"}
                                            ]                                          
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ningun artista",
                    content = @Content),
    })
    @GetMapping("/artist/")
    public ResponseEntity<List<Artist>> getAllArtists() {
        if(repo.findAll().isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.ok(repo.findAll());
        }
    }

    @Operation(summary = "Obtiene un artista por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha encontrado el artista",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class),
                            examples = {@ExampleObject(
                                    value = """
                                                {"id": 1, "name": "Joaquin Sabina"}                      
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ningun artista",
                    content = @Content),
    })
    @GetMapping("/artist/{id}")
    public ResponseEntity<Artist> getArtistById(@Parameter(description = " ID del artista a consultar")@PathVariable Long id) {
        return ResponseEntity.of(repo.findById(id));
    }


    @Operation(summary = "Añade un nuevo artista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Se ha creado satisfactoriamente el artista",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class),
                            examples = {@ExampleObject(
                                    value = """
                                                {"id": 1, "name": "Joaquin Sabina"}                                                                             
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400",
                    description = "Ha habido un error en los datos recibidos",
                    content = @Content),
    })
    @PostMapping("/artist/")
    public ResponseEntity<Artist> newArtist(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = " Objeto tipo artista necesario para su creacion") @RequestBody Artist a) {
        if(a.getName() == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repo.save(a));
    }

    @Operation(summary = "Modifica el artista con ID especificado por el artista recibido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha realizado correctamente la edicion",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class),
                            examples = {@ExampleObject(
                                    value = """
                                                {"id": 1, "name": "Joaquin Sabina"}                                                                             
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404",
                    description = "No se ha encontrado ningun artista",
                    content = @Content),
    })
    @PutMapping("/artist/{id}")
    public ResponseEntity<Artist> editArtist(@Parameter(description = " ID del artista a editar")@PathVariable Long id,
                                             @io.swagger.v3.oas.annotations.parameters.RequestBody(description = " Objeto tipo artista necesario para la edicion del artista") @RequestBody Artist a){
        return ResponseEntity.of(repo.findById(id)
                .map(old ->{
                    old.setName(a.getName());
                    return Optional.of(repo.save(old));
                }).orElse(Optional.empty())
        );
    }

    @Operation(summary = "Elimina el artista con ID especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Se ha realizado correctamente la eliminacion, y por tanto no hay contenido",
                    content = @Content
                    )
    })
    @DeleteMapping("/artist/{id}")
    public ResponseEntity<?> deleteArtist(@Parameter(description = " ID del artista a eliminar")@PathVariable Long id){
        List<Song> songList = repoSong.findAll();
        Artist artist = repo.findById(id).orElse(null);

        if(repo.existsById(id)){
            for(int i =0; i< songList.size(); i++){
                if(songList.get(i).getArtist() == artist){
                    songList.get(i).setArtist(null);
                }
            }
            repo.deleteById(id);
        }


        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
