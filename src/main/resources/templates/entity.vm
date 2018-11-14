package com.wy.entity;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import com.google.common.base.MoreObjects;
import com.wy.annotation.CheckAdd;
import com.wy.annotation.CheckUpdate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@Table("ti_button")
public class Button extends BaseBean<Button> {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(hump = true)
	@CheckUpdate
	private Integer buttonId;

	@Column(hump = true)
	@CheckAdd
	private String buttonName;

	@Column(hump = true)
	@CheckAdd
	private Integer menuId;

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).omitNullValues().add("buttonId", buttonId)
				.add("buttonName", buttonName).add("menuId", menuId).toString();
	}
}