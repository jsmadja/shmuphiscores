package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import org.ocpsoft.prettytime.PrettyTime;
import play.db.ebean.Model;
import play.mvc.Http;
import play.mvc.PathBindable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.Locale;

@Entity
public abstract class BaseModel<T extends BaseModel<T>> extends Model implements PathBindable<T> {

    @Id
    public Long id;

    @CreatedTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @UpdatedTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public BaseModel() {
    }

    public Finder<Long, T> getFinder() {
        return new Model.Finder(Long.class, this.getClass());
    }

    @Override
    public void update() {
        updatedAt = new Date();
        super.update();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseModel baseModel = (BaseModel) o;
        return !(id != null ? !id.equals(baseModel.id) : baseModel.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public T bind(String key, String value) {
        return (T) getFinder().byId(Long.valueOf(value));
    }

    @Override
    public String unbind(String s) {
        return id.toString();
    }

    @Override
    public String javascriptUnbind() {
        return null;
    }

    public String getCreatedSince() {
        if (createdAt == null) {
            return "";
        }
        return new PrettyTime(Http.Context.current().lang().toLocale()).format(createdAt);
    }

    public String getUpdatedSince() {
        if (updatedAt == null) {
            return "";
        }
        return new PrettyTime(Http.Context.current().lang().toLocale()).format(updatedAt);
    }

    public String getCreatedSinceInFrench() {
        if (createdAt == null) {
            return "";
        }
        return new PrettyTime(Locale.FRENCH).format(createdAt);
    }

}
