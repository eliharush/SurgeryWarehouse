package il.swhm.shared.entities;

import il.swhm.shared.entities.inventorycount.InventoryCount;
import il.swhm.shared.entities.order.PushOrderHead;
import il.swhm.shared.enums.EntityType;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonAutoDetect
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="@class")
@JsonSubTypes({
	@JsonSubTypes.Type(value=InventoryCount.class,name="InventoryCount"),
	@JsonSubTypes.Type(value=PushOrderHead.class,name="PushOrderHead"),
	})
public abstract class Transmitable {
	public abstract EntityType getEntityType();
	public void setEntityType(EntityType type){
		//do nothing
	}
}
