package com.btg.challenge.friend;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping
    public ResponseEntity<FriendDto> createFriend(@RequestBody FriendDto friendDto) {
        return ResponseEntity.status(201).body(friendService.save(friendDto));
    }

    @GetMapping
    public ResponseEntity<Page<FriendDto>> getAllFriends(Pageable pageable) {
        return ResponseEntity.ok(friendService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendDto> getFriendById(@PathVariable Long id) {
        return ResponseEntity.ok(friendService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FriendDto> updateFriend(@PathVariable Long id, @RequestBody FriendDto friendDto) {
        return ResponseEntity.ok(friendService.update(id, friendDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long id) {
        friendService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
