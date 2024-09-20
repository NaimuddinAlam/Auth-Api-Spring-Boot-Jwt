package com.user.auth.controller;

import com.user.auth.model.AppRole;
import com.user.auth.model.Role;
import com.user.auth.model.User;
import com.user.auth.repositorys.RoleRepository;
import com.user.auth.repositorys.UserRepository;
import com.user.auth.security.jwt.JwtUtils;
import com.user.auth.security.request.LoginRequest;
import com.user.auth.security.request.SignupRequest;
import com.user.auth.security.response.MessageResponse;
import com.user.auth.security.response.UserInfoResponse;
import com.user.auth.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
     @Autowired
    private JwtUtils jwtUtils;
     @Autowired
    private AuthenticationManager authenticationManager;
     @Autowired
    private UserRepository userRepository;
     @Autowired
    private RoleRepository roleRepository;
     @Autowired
    private PasswordEncoder passwordEncoder;

     @PostMapping("/signin")
   public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest)
     {
         Authentication authentication;
       try {
           authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName(),loginRequest.getPassword()));
       }catch (AuthenticationException e)
       {
           Map<String,Object> map= new HashMap<>();
           map.put("message","Bed credentials");
           map.put("status",false);
          return new ResponseEntity<Object>(map,HttpStatus.NOT_FOUND);

       }
         SecurityContextHolder.getContext().setAuthentication(authentication);
         UserDetailsImpl userDetails=(UserDetailsImpl)authentication.getPrincipal();
         ResponseCookie responseCookie=jwtUtils.generateJwtCookie(userDetails);
         List<String> roles=userDetails.getAuthorities().stream()
                 .map(GrantedAuthority::getAuthority)
                 .toList();

         UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                 userDetails.getUsername(), roles, responseCookie.toString());

         return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                         responseCookie.toString())
                 .body(response);
     }

     @PostMapping("/signup")
    public  ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest)
     {
       if(userRepository.existsByUserName(signUpRequest.getUsername()))
       {
       return  ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));

       }
       if(userRepository.existsByEmail(signUpRequest.getEmail()))
       {
           return  ResponseEntity.badRequest().body(new MessageResponse("Error: UEmail is already in use!"));

       }

         User user= new User(signUpRequest.getUsername(),signUpRequest.getEmail(),
                 passwordEncoder.encode(  signUpRequest.getPassword())
               );
       Set<String> roles=signUpRequest.getRole();
Set<Role> roleSet= new HashSet<>();
if(roles==null)
{
Role role=roleRepository.findByRoleName(AppRole.ROLE_USER)
        .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
roleSet.add(role);
}else {
    roles.forEach(role -> {
switch (role)
{
    case "admin":
        Role roleAdmin=roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                .orElseThrow( ()-> new RuntimeException("Error: Role is not found."));
        roleSet.add(roleAdmin);
        break;
    case "seller":
        Role roleseller=roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                .orElseThrow(()-> new RuntimeException("Error: Role is not found"));
        roleSet.add(roleseller);
        break;
    default:
        Role userAdmin=roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow( ()-> new RuntimeException("Error: Role is not found."));
        roleSet.add(userAdmin);
        break;

}
    });
}
user.setRoles(roleSet);
userRepository.save(user);

         return ResponseEntity.ok(new MessageResponse("User registered successfully!"));


     }

     @GetMapping("/username")
    public String currentUserName(Authentication authentication)
     {
         if(authentication!=null)

             return  authentication.getName();
        else
             return  "";
     }
     @GetMapping("/user")
  public ResponseEntity<?> getUserDetails(Authentication authentication)
     {
       UserDetailsImpl userDetails=(UserDetailsImpl) authentication.getPrincipal();
      List<String> roles=userDetails.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.toList());

         UserInfoResponse userInfoResponse=new UserInfoResponse(userDetails.getId(),
                 userDetails.getUsername(),roles);
         return ResponseEntity.ok().body(userInfoResponse);
     }
@PostMapping("/signout")
public  ResponseEntity<?> signoutUser()
{
   ResponseCookie cookie=jwtUtils.getCleanJwtCookie();
   return  ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
           cookie.toString()).body(new MessageResponse("You've been signed out!"));
}




}
