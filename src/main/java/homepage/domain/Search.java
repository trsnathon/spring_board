package homepage.domain;

import lombok.Data;


public class Search {
     String searchTitle;

     public Search(String searchTitle) {
          this.searchTitle = searchTitle;
     }

     public String getSearchTitle() {
          return searchTitle;
     }

     public void setSearchTitle(String searchTitle) {
          this.searchTitle = searchTitle;
     }

     public Search() {
     }
}
