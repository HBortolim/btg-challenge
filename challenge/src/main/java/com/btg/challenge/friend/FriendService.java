package com.btg.challenge.friend;

import com.btg.challenge.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;

    public FriendService(FriendRepository friendRepository, FriendMapper friendMapper) {
        this.friendRepository = friendRepository;
        this.friendMapper = friendMapper;
    }

    public FriendDto save(FriendDto friendDto) {
        Friend friend = friendMapper.toEntity(friendDto);
        return friendMapper.toDto(friendRepository.save(friend));
    }

    public Page<FriendDto> findAll(Pageable pageable) {
        return friendRepository.findAll(pageable)
                .map(friendMapper::toDto);
    }

    public FriendDto findById(Long id) {
        return friendRepository.findById(id)
                .map(friendMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found with id: " + id));
    }

    public void deleteById(Long id) {
        friendRepository.deleteById(id);
    }

    public FriendDto update(Long id, FriendDto friendDto) {
        Friend existingFriend = friendRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found with id: " + id));
        existingFriend.setName(friendDto.getName());
        return friendMapper.toDto(friendRepository.save(existingFriend));
    }
}
