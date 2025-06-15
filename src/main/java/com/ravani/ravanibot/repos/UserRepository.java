package com.ravani.ravanibot.repos;

import com.ravani.ravanibot.entities.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<BotUser, Long> {
    Optional<BotUser> findByChatId(Long chatId);
    Optional<BotUser> findByName(String name);
}
