package com.okhara.rating_system.service.open;

import com.okhara.rating_system.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentOpenService {

    private final CommentRepository commentRepository;

}