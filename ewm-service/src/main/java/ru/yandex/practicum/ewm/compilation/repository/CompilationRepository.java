package ru.yandex.practicum.ewm.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ewm.compilation.model.Compilation;

import java.util.Collection;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long>, CompilationRepositoryCustom {

    @Query("SELECT c FROM Compilation c " +
            "WHERE (?1 IS NULL OR c.pinned = ?1)")
    Page<Compilation> getCompilations(Boolean pinned, Pageable pageable);

    boolean existsByPinnedIsAndId(boolean pinned, long eventId);

    @Query(nativeQuery = true, value = "INSERT INTO events_compilations VALUES (?1, ?2)")
    @Modifying
    void addEventIntoCompilation(Long eventId, long compId);

    @Query(nativeQuery = true, value = "DELETE FROM events_compilations WHERE ref_event IN ?1 AND ref_compilation = ?2")
    @Modifying
    void removeEventsFromCompilation(Collection<Long> eventIds, long compId);

    @Query("UPDATE Compilation c " +
            "SET c.pinned = ?1 " +
            "WHERE c.id = ?2")
    @Modifying
    void changePinned(boolean pinned, long eventId);
}
