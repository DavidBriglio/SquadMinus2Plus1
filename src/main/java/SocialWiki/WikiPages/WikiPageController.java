package SocialWiki.WikiPages;

import SocialWiki.Users.User;
import SocialWiki.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Chris on 2/24/2017.
 */
@RestController
public class WikiPageController {

    /**
     * Repository for all WikiPages.
     */
    @Autowired
    private WikiPageRepository wikiPageRepo;

    /**
     * Repository for all Users.
     */
    @Autowired
    private UserRepository userRepo;

    /**
     * Method to handle the creation or editing of a WikiPage
     * @param request - contains the title, content, parentID, and authorID of the WikiPage being created/altered
     * @return the new WikiPage
     */
    @PostMapping("/createWikiPage")
    public ResponseEntity<WikiPage> createWikiPage(HttpServletRequest request) {

        //Retrieve parameters from request
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String username = request.getParameter("username");

        User user;

        Long parentID, authorID;

        try {
            parentID = Long.parseLong(request.getParameter("parentID"));
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(new WikiPage(), HttpStatus.PRECONDITION_FAILED);
        }

        try {
            authorID = Long.parseLong(request.getParameter("authorID"));
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(new WikiPage(), HttpStatus.PRECONDITION_FAILED);
        }

        //Validate parameters

        if (title == null || title.isEmpty()) {    //title must be valid, non-empty string
            return new ResponseEntity<>(new WikiPage(), HttpStatus.PRECONDITION_FAILED);
        }
        else if (content == null) {     //content must be valid string
            return new ResponseEntity<>(new WikiPage(), HttpStatus.PRECONDITION_FAILED);
        }
        else if (parentID.compareTo(0L) == 0 || parentID.compareTo(-1L) < 0) {  //Parent ID must be > 0 or -1
            return new ResponseEntity<>(new WikiPage(), HttpStatus.PRECONDITION_FAILED);
        }
        else if (authorID.compareTo(0L) <= 0) {   //Author ID must be > 0
            return new ResponseEntity<>(new WikiPage(), HttpStatus.PRECONDITION_FAILED);
        }

        List<User> authorQuery = userRepo.findByUserName(username);

        //Username must be valid
        if (authorQuery.size() == 1) {
            user = authorQuery.get(0);
        } else {
            return ResponseEntity.status(422).body(null);
        }

        WikiPage newPage;

        //If the WikiPage being created has no predecessor and is original then use specific constructor
        if (parentID.compareTo(WikiPage.IS_ORIGINAL_ID) == 0) {
            newPage = new WikiPage(title, content, authorID, user);
        }
        else {
            newPage = new WikiPage(title, content, parentID, user);
        }

        //Save the WikiPage
        try {
            newPage = wikiPageRepo.save(newPage);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(newPage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(newPage, HttpStatus.OK);
    }

    /**
     * Method to handle searching for list of WikiPages
     * @param request - contains the parameters of the WikiPages being searched for
     * @return the list of WikiPages found
     */
    @GetMapping("/searchWikiPage")
    public ResponseEntity<List<WikiPage>> searchWikiPage(HttpServletRequest request) {

        //Retrieve parameters from request
        String title = request.getParameter("title");
        String authorUserName = request.getParameter("author");
        String content = request.getParameter("content");

        Long userID = null;

        if ((title == null || title.isEmpty()) &&
                (authorUserName == null || authorUserName.isEmpty()) &&
                (content == null || content.isEmpty()) ) {    //If all parameters are empty
            return ResponseEntity.status(422).body(null);   //Error 422 for un-processable Identity
        }

        List<User> authorQuery = userRepo.findByUserName(authorUserName);

        if (authorQuery.size() == 1) {
            userID = authorQuery.get(0).getId();
        }

        List<WikiPage> pages = wikiPageRepo.findByTitleAndAuthorIDAndContent(title, userID, content);

        return new ResponseEntity<>(pages, HttpStatus.OK);

    }

}
