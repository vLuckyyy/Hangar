package io.papermc.hangar.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.papermc.hangar.controller.extras.requestmodels.api.RequestPagination;
import io.papermc.hangar.model.api.PaginatedResult;
import io.papermc.hangar.model.api.User;
import io.papermc.hangar.modelold.ApiAuthInfo;
import io.papermc.hangar.service.api.UsersApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path = {"/api", "/api/v1"}, produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.GET, RequestMethod.POST})
public class UsersController {

    private final UsersApiService usersApiService;
    private final ApiAuthInfo apiAuthInfo;

    @Autowired
    public UsersController(UsersApiService usersApiService, ApiAuthInfo apiAuthInfo) {
        this.usersApiService = usersApiService;
        this.apiAuthInfo = apiAuthInfo;
    }

    @GetMapping("/users/{user}")
    @PreAuthorize("@authenticationService.authApiRequest(T(io.papermc.hangar.model.Permission).ViewPublicInfo, T(io.papermc.hangar.controller.extras.ApiScope).ofGlobal())")
    public ResponseEntity<User> getUser(@PathVariable("user") String userName) throws JsonProcessingException {
        return ResponseEntity.ok(usersApiService.getUser(userName, User.class));
    }

    @GetMapping("/users")
    @PreAuthorize("@authenticationService.authApiRequest(T(io.papermc.hangar.model.Permission).ViewPublicInfo, T(io.papermc.hangar.controller.extras.ApiScope).ofGlobal())")
    public ResponseEntity<PaginatedResult<User>> getUsers(@RequestParam("q") String query, RequestPagination pagination) {
        return ResponseEntity.ok(usersApiService.getUsers(query, pagination, User.class));
    }
}
