package io.hypersistence.optimizer.hibernate.event.session;

import io.hypersistence.optimizer.util.AbstractHypersistenceOptimizerTest;
import jakarta.persistence.*;

import java.util.Date;

public class TableRowAlreadyManagedEventTest extends AbstractHypersistenceOptimizerTest {
    @Override
    protected void verify() {
        doInHibernate(entityManager -> {
            final Post post = new Post();
            post.setTitle("High-Performance Java Persistence");
            post.setOwner("Vlad Mihalcea");
            post.setContent("The best book ever written about Java Persistence");

            final Announcement announcement = new Announcement();
            announcement.setTitle("High-Performance Java Persistence discount just for you!");
            announcement.setOwner("Vlad Mihalcea");
            announcement.setValidUntil(new Date(System.currentTimeMillis() + 86_400_000));

            entityManager.persist(post);
            entityManager.persist(announcement);

            entityManager.find(Post.class, post.getId());
            entityManager.find(Announcement.class, announcement.getId());

            assertEventTriggered(0, TableRowAlreadyManagedEvent.class);
        });
    }

    @Override
    public Class<?>[] entities() {
        return new Class<?>[]{
                Topic.class,
                Post.class,
                Announcement.class
        };
    }

    @Entity
    @Table(name = "topic")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(
            discriminatorType = DiscriminatorType.INTEGER,
            name = "topic_type_id",
            columnDefinition = "TINYINT"
    )
    public abstract class Topic {
        @Id
        @GeneratedValue
        private Long id;

        private String title;

        private String owner;

        @Temporal(TemporalType.TIMESTAMP)
        private Date createdOn = new Date();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public Date getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(Date createdOn) {
            this.createdOn = createdOn;
        }
    }

    @Entity
    @DiscriminatorValue("1")
    public class Post extends Topic {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @Entity
    @DiscriminatorValue("2")
    public class Announcement extends Topic {
        @Temporal(TemporalType.TIMESTAMP)
        private Date validUntil;

        public Date getValidUntil() {
            return validUntil;
        }

        public void setValidUntil(Date validUntil) {
            this.validUntil = validUntil;
        }
    }

}
