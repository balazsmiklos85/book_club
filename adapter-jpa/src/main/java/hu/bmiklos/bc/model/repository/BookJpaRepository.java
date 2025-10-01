package hu.bmiklos.bc.model.repository;

import hu.bmiklos.bc.model.entity.BookEntity;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookJpaRepository extends JpaRepository<BookEntity, UUID> {
  Collection<BookEntity> findByEventsIsEmpty();

  @Query(
      value =
          """
            select book_id, sum(user_weight)
            from (select p.participant_external_id as user_external_id, vote_union.book_id, count(p.participant_external_id) as user_weight
            	  from participants p
            	  inner join (select v.user_external_id , v.book_id
            	              from votes v
            	              where v.user_external_id is not null
            	              union
            	              select u.external_id , v.book_id
            	              from votes v
            	              inner join users u
            	              on u.id  = v.user_id) as vote_union
            	    on vote_union.user_external_id = p.participant_external_id
            	  where p.event_id in (select id
            	                       from (select distinct e.id, e.time
            	                             from events e
            	                             inner join participants p
            	                               on p.event_id = e.id
            	                             order by e.time desc
            	                             limit 12))
            	  group by p.participant_external_id, vote_union.book_id)
            group by book_id
            order by sum(user_weight) desc
            """,
      nativeQuery = true)
  List<Object[]> findBookWeights();
}
