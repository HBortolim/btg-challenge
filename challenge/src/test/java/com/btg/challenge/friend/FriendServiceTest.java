package com.btg.challenge.friend;

import com.btg.challenge.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FriendServiceTest {
    @Mock
    private FriendRepository friendRepository;

    @Spy
    private FriendMapper friendMapper = Mappers.getMapper(FriendMapper.class);

    @InjectMocks
    private FriendService friendService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenSaveFriendShouldReturnSavedFriendDto() {
        FriendDto friendDto = new FriendDto();
        friendDto.setName("John Doe");

        Friend friend = new Friend();
        friend.setName("John Doe");

        when(friendMapper.toEntity(any(FriendDto.class))).thenReturn(friend);
        when(friendRepository.save(any(Friend.class))).thenReturn(friend);
        when(friendMapper.toDto(any(Friend.class))).thenReturn(friendDto);

        friendService.save(friendDto);

        verify(friendRepository, times(1)).save(any(Friend.class));
    }

    @Test
    public void whenFindByIdWithValidIdShouldReturnFriendDto() {
        Friend friend = new Friend();
        friend.setId(1L);
        FriendDto friendDto = new FriendDto();
        friendDto.setId(1L);

        when(friendRepository.findById(1L)).thenReturn(Optional.of(friend));
        when(friendMapper.toDto(any(Friend.class))).thenReturn(friendDto);

        friendService.findById(1L);

        verify(friendRepository, times(1)).findById(1L);
    }

    @Test
    public void whenFindByIdWithNonExistentIdShouldThrowResourceNotFoundException() {
        when(friendRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> friendService.findById(1L));
    }

    @Test
    public void whenFindAllShouldReturnPageOfFriendDtos() {
        Page<Friend> friendPage = new PageImpl<>(Collections.singletonList(new Friend()));
        when(friendRepository.findAll(any(Pageable.class))).thenReturn(friendPage);

        friendService.findAll(Pageable.unpaged());

        verify(friendRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void whenUpdateWithValidDataShouldReturnUpdatedFriendDto() {
        Friend friend = new Friend();
        friend.setId(1L);
        FriendDto friendDto = new FriendDto();
        friendDto.setId(1L);
        friendDto.setName("Jane Doe");

        when(friendRepository.findById(1L)).thenReturn(Optional.of(friend));
        when(friendRepository.save(any(Friend.class))).thenReturn(friend);

        friendService.update(1L, friendDto);

        verify(friendRepository, times(1)).save(any(Friend.class));
    }

    @Test
    public void whenUpdateWithNonExistentIdShouldThrowResourceNotFoundException() {
        FriendDto friendDto = new FriendDto();
        friendDto.setId(1L);
        friendDto.setName("Jane Doe");

        when(friendRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> friendService.update(1L, friendDto));
    }

    @Test
    public void whenDeleteByIdShouldCallRepositoryDelete() {
        doNothing().when(friendRepository).deleteById(1L);
        friendService.deleteById(1L);
        verify(friendRepository, times(1)).deleteById(1L);
    }
}
