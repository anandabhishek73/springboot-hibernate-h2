package com.abhishek.demo.db.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Slf4j
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(columnDefinition = "integer default 0")
    private Integer version;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;

//    @Override
//    public String toString() {
//        return "BaseEntity{" +
//                "id=" + id +
//                ", version=" + version +
//                ", createDate=" + createDate +
//                ", modifyDate=" + modifyDate +
//                '}';
//    }

    /**
     * Uniquely identifies a row of database table. All extending subclasses will have different hash codes even
     * for same 'ID' field in DB. Even if multiple entity objects are created in application context, after independently
     * fetching them from same/different entity managers, the hashCode will remain same throughout the application.
     * Modification to an attribute does not change the hash code for that entity instance.
     *
     * @return A unique integer representation of the entity object, consistent throughout a single application run.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id) +           // same for new non-persisted entity instances. Different once persisted
                Objects.hashCode(getClass());   // different for each subclass of this entity
    }

    /**
     * Performs a comparison between this and that objects and return true if the instance state is same for both
     * the object.
     * @param that - the object to be compared with this one
     * @return - true, if
     */
    @Override
    public boolean equals(Object that) {
        if (null == that) return false;      // NPE check
        if (super.equals(that)) return true; // reflexive
        if (getClass() != that.getClass()) return false;
        if (hashCode() != that.hashCode()) return false;
        BaseEntity other = (BaseEntity) that;
        return id.equals(other.getId()) && version.equals(other.getVersion());
    }
}
