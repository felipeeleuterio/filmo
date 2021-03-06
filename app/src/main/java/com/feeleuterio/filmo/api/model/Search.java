package com.feeleuterio.filmo.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "page",
        "total_results",
        "total_pages",
        "results"
})
public class Search {

    @JsonProperty("page")
    public int page;
    @JsonProperty("total_results")
    public int totalResults;
    @JsonProperty("total_pages")
    public int totalPages;
    @JsonProperty("results")
    public List<Movie> movies = null;

}