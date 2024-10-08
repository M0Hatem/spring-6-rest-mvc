package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/beer")
public class BeerController {
    private final BeerService beerService;

    @PatchMapping("{beerId}")
    public ResponseEntity patchById(@PathVariable("beerId")  UUID beerId, @RequestBody Beer beer){
        beerService.patchBeerById(beerId,beer);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{beerId}")
    public ResponseEntity DeleteById(@PathVariable(("beerId")) UUID beerId){

        beerService.deleteById(beerId);

        return new ResponseEntity(HttpStatus.NO_CONTENT );
    }

    @PutMapping("{beerId}")
    public ResponseEntity updateById(@PathVariable("beerId")  UUID beerId, @RequestBody Beer beer){
        beerService.updateBeerById(beerId,beer);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location","/api/v1/beer/"+beer.getId());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity handlePost(@RequestBody Beer beer){
         Beer savedBeer = beerService.saveNewBeer(beer);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location","/api/v1/beer/" + savedBeer.getId().toString());
         return new ResponseEntity(headers,HttpStatus.CREATED);
    }

    @GetMapping
    public List<Beer> listBeers(){
        return beerService.listBeers();
    }


    @RequestMapping(value = "/{beerId}",method = RequestMethod.GET)
    public Beer getBeerByID(@PathVariable("beerId")  UUID beerId){
        System.out.println(beerId);
        log.debug("get Beer id controller was called");
        return beerService.getById(beerId);
    }
}
