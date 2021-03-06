package com.michaelfmnk.aldrin.entities;


import com.michaelfmnk.aldrin.dtos.PostDto;
import com.michaelfmnk.aldrin.utils.SortingInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Data
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
@Entity
public class Post implements Updatable<PostDto> {

    public static final String POST_ID = "postId";
    public static final String DATE = "date";
    public static final String AUTHOR = "author";
    public static final SortingInfo SORTING_INFO = new SortingInfo(DATE);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @Length(max = 300, message = "title must not contain more then 300 characters")
    private String title;

    @CreationTimestamp
    private LocalDateTime date;

    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @OrderBy("date DESC")
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "likes",
            joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "postId"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userId")
    )
    private Set<User> likes;

    @Override
    public void update(PostDto dto) {
        this.title = dto.getTitle();
    }
}
