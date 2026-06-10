from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

DOMAIN_DIR = ROOT / "ruoyi-system/src/main/java/com/ruoyi/system/domain/space"
MAPPER_DIR = ROOT / "ruoyi-system/src/main/java/com/ruoyi/system/mapper/space"
SERVICE_DIR = ROOT / "ruoyi-system/src/main/java/com/ruoyi/system/service/space"
SERVICE_IMPL_DIR = ROOT / "ruoyi-system/src/main/java/com/ruoyi/system/service/space/impl"
XML_DIR = ROOT / "ruoyi-system/src/main/resources/mapper/space"
CONTROLLER_DIR = ROOT / "ruoyi-admin/src/main/java/com/ruoyi/web/controller/space"
API_DIR = ROOT / "ruoyi-ui/src/api/space"
VIEW_DIR = ROOT / "ruoyi-ui/src/views/space"

for d in [DOMAIN_DIR, MAPPER_DIR, SERVICE_DIR, SERVICE_IMPL_DIR, XML_DIR, CONTROLLER_DIR, API_DIR, VIEW_DIR]:
    d.mkdir(parents=True, exist_ok=True)


TABLES = [
    {
        "class": "SpaceOrg",
        "name": "space_org",
        "pk": ("orgId", "org_id", "Long"),
        "title": "学校/组织",
        "base_path": "org",
        "permi": "space:org",
        "fields": [
            ("orgCode", "org_code", "String", "组织编码"),
            ("orgName", "org_name", "String", "组织名称"),
            ("orgShortName", "org_short_name", "String", "组织简称"),
            ("orgType", "org_type", "String", "组织类型"),
            ("deptId", "dept_id", "Long", "部门ID"),
            ("contactName", "contact_name", "String", "联系人"),
            ("contactPhone", "contact_phone", "String", "联系电话"),
            ("status", "status", "String", "状态"),
            ("delFlag", "del_flag", "String", "删除标志"),
        ],
    },
    {
        "class": "SpaceBuilding",
        "name": "space_building",
        "pk": ("buildingId", "building_id", "Long"),
        "title": "楼栋",
        "base_path": "building",
        "permi": "space:building",
        "fields": [
            ("buildingCode", "building_code", "String", "楼栋编码"),
            ("buildingName", "building_name", "String", "楼栋名称"),
            ("campusName", "campus_name", "String", "校区"),
            ("address", "address", "String", "地址"),
            ("floorCount", "floor_count", "Integer", "楼层数量"),
            ("status", "status", "String", "状态"),
            ("delFlag", "del_flag", "String", "删除标志"),
        ],
    },
    {
        "class": "SpaceRoomType",
        "name": "space_room_type",
        "pk": ("typeId", "type_id", "Long"),
        "title": "房间类型",
        "base_path": "room/type",
        "permi": "space:roomType",
        "fields": [
            ("typeCode", "type_code", "String", "类型编码"),
            ("typeName", "type_name", "String", "类型名称"),
            ("capacityLevel", "capacity_level", "String", "容量等级"),
            ("orderNum", "order_num", "Integer", "显示顺序"),
            ("status", "status", "String", "状态"),
        ],
    },
    {
        "class": "SpaceEquipment",
        "name": "space_equipment",
        "pk": ("equipmentId", "equipment_id", "Long"),
        "title": "设备",
        "base_path": "equipment",
        "permi": "space:equipment",
        "fields": [
            ("equipmentCode", "equipment_code", "String", "设备编码"),
            ("equipmentName", "equipment_name", "String", "设备名称"),
            ("orderNum", "order_num", "Integer", "显示顺序"),
            ("status", "status", "String", "状态"),
        ],
    },
    {
        "class": "SpaceRoomEquipment",
        "name": "space_room_equipment",
        "pk": ("roomEquipmentId", "room_equipment_id", "Long"),
        "title": "房间设备",
        "base_path": "room/equipment",
        "permi": "space:roomEquipment",
        "fields": [
            ("roomId", "room_id", "Long", "房间ID"),
            ("equipmentId", "equipment_id", "Long", "设备ID"),
            ("quantity", "quantity", "Integer", "数量"),
            ("status", "status", "String", "状态"),
        ],
    },
    {
        "class": "SpaceTimePeriod",
        "name": "space_time_period",
        "pk": ("periodId", "period_id", "Long"),
        "title": "标准时段",
        "base_path": "time-period",
        "permi": "space:timePeriod",
        "fields": [
            ("periodCode", "period_code", "String", "时段编码"),
            ("periodName", "period_name", "String", "时段名称"),
            ("startTime", "start_time", "String", "开始时间"),
            ("endTime", "end_time", "String", "结束时间"),
            ("orderNum", "order_num", "Integer", "显示顺序"),
            ("status", "status", "String", "状态"),
        ],
    },
    {
        "class": "SpaceRoom",
        "name": "space_room",
        "pk": ("roomId", "room_id", "Long"),
        "title": "房间",
        "base_path": "room",
        "permi": "space:room",
        "imports": ["java.math.BigDecimal"],
        "fields": [
            ("roomCode", "room_code", "String", "房间编号"),
            ("roomName", "room_name", "String", "房间名称"),
            ("buildingId", "building_id", "Long", "楼栋ID"),
            ("buildingName", "building_name", "String", "楼栋名称"),
            ("floorNo", "floor_no", "String", "楼层"),
            ("typeId", "type_id", "Long", "类型ID"),
            ("roomType", "room_type", "String", "房间类型"),
            ("area", "area", "BigDecimal", "面积"),
            ("capacityMin", "capacity_min", "Integer", "最小容量"),
            ("capacityMax", "capacity_max", "Integer", "最大容量"),
            ("capacityDesc", "capacity_desc", "String", "容量描述"),
            ("assignedOrgId", "assigned_org_id", "Long", "建议分配组织ID"),
            ("assignedOrgName", "assigned_org_name", "String", "建议分配组织"),
            ("equipmentDesc", "equipment_desc", "String", "设备描述"),
            ("locationDesc", "location_desc", "String", "位置描述"),
            ("bookable", "bookable", "String", "是否可预约"),
            ("status", "status", "String", "状态"),
            ("delFlag", "del_flag", "String", "删除标志"),
        ],
    },
    {
        "class": "SpaceReservation",
        "name": "space_reservation",
        "pk": ("reservationId", "reservation_id", "Long"),
        "title": "预约申请",
        "base_path": "reservation",
        "permi": "space:reservation",
        "imports": ["java.util.Date", "java.util.List"],
        "fields": [
            ("reservationNo", "reservation_no", "String", "预约编号"),
            ("reservationType", "reservation_type", "String", "预约类型"),
            ("applicantId", "applicant_id", "Long", "申请人ID"),
            ("applicantName", "applicant_name", "String", "申请人"),
            ("applicantRole", "applicant_role", "String", "申请人角色"),
            ("applicantPhone", "applicant_phone", "String", "联系电话"),
            ("orgId", "org_id", "Long", "组织ID"),
            ("orgName", "org_name", "String", "组织名称"),
            ("title", "title", "String", "预约主题"),
            ("purpose", "purpose", "String", "用途"),
            ("peopleCount", "people_count", "Integer", "预约人数"),
            ("detailRemark", "detail_remark", "String", "详细备注"),
            ("status", "status", "String", "状态"),
            ("submitTime", "submit_time", "Date", "提交时间"),
            ("auditorId", "auditor_id", "Long", "审核人ID"),
            ("auditorName", "auditor_name", "String", "审核人"),
            ("auditTime", "audit_time", "Date", "审核时间"),
            ("rejectReason", "reject_reason", "String", "驳回原因"),
            ("version", "version", "Integer", "版本"),
            ("delFlag", "del_flag", "String", "删除标志"),
        ],
        "extra_fields": [
            ("items", "List<SpaceReservationItem>", "预约场次"),
            ("rule", "SpaceReservationRule", "长期规则"),
        ],
    },
    {
        "class": "SpaceReservationRule",
        "name": "space_reservation_rule",
        "pk": ("ruleId", "rule_id", "Long"),
        "title": "长期预约规则",
        "base_path": "reservation/rule",
        "permi": "space:reservationRule",
        "fields": [
            ("reservationId", "reservation_id", "Long", "预约ID"),
            ("ruleType", "rule_type", "String", "规则类型"),
            ("roomId", "room_id", "Long", "默认房间ID"),
            ("roomCode", "room_code", "String", "默认房间"),
            ("startDate", "start_date", "String", "开始日期"),
            ("endDate", "end_date", "String", "结束日期"),
            ("weekdays", "weekdays", "String", "重复星期"),
            ("customDatesText", "custom_dates_text", "String", "自定义日期"),
            ("startTime", "start_time", "String", "开始时间"),
            ("endTime", "end_time", "String", "结束时间"),
            ("ruleDesc", "rule_desc", "String", "规则描述"),
        ],
    },
    {
        "class": "SpaceReservationItem",
        "name": "space_reservation_item",
        "pk": ("itemId", "item_id", "Long"),
        "title": "预约场次",
        "base_path": "reservation/item",
        "permi": "space:reservationItem",
        "imports": ["java.util.Date"],
        "fields": [
            ("reservationId", "reservation_id", "Long", "预约ID"),
            ("roomId", "room_id", "Long", "房间ID"),
            ("roomCode", "room_code", "String", "房间编号"),
            ("roomName", "room_name", "String", "房间名称"),
            ("bookingDate", "booking_date", "String", "预约日期"),
            ("weekday", "weekday", "String", "星期"),
            ("startTime", "start_time", "String", "开始时间"),
            ("endTime", "end_time", "String", "结束时间"),
            ("itemStatus", "item_status", "String", "场次状态"),
            ("conflictFlag", "conflict_flag", "String", "冲突标识"),
            ("conflictReason", "conflict_reason", "String", "冲突原因"),
            ("conflictItemId", "conflict_item_id", "Long", "冲突场次ID"),
            ("auditTime", "audit_time", "Date", "审核时间"),
            ("auditorId", "auditor_id", "Long", "审核人ID"),
            ("auditorName", "auditor_name", "String", "审核人"),
            ("rejectReason", "reject_reason", "String", "驳回原因"),
        ],
    },
    {
        "class": "SpaceAuditLog",
        "name": "space_audit_log",
        "pk": ("logId", "log_id", "Long"),
        "title": "审核日志",
        "base_path": "audit-log",
        "permi": "space:auditLog",
        "imports": ["java.util.Date"],
        "fields": [
            ("reservationId", "reservation_id", "Long", "预约ID"),
            ("itemId", "item_id", "Long", "场次ID"),
            ("auditAction", "audit_action", "String", "审核动作"),
            ("beforeStatus", "before_status", "String", "操作前状态"),
            ("afterStatus", "after_status", "String", "操作后状态"),
            ("auditorId", "auditor_id", "Long", "操作人ID"),
            ("auditorName", "auditor_name", "String", "操作人"),
            ("auditOpinion", "audit_opinion", "String", "意见"),
            ("auditTime", "audit_time", "Date", "审核时间"),
        ],
    },
    {
        "class": "SpaceBlackout",
        "name": "space_blackout",
        "pk": ("blackoutId", "blackout_id", "Long"),
        "title": "维护停用",
        "base_path": "blackout",
        "permi": "space:blackout",
        "fields": [
            ("roomId", "room_id", "Long", "房间ID"),
            ("roomCode", "room_code", "String", "房间编号"),
            ("startTime", "start_time", "String", "停用开始"),
            ("endTime", "end_time", "String", "停用结束"),
            ("reason", "reason", "String", "原因"),
            ("status", "status", "String", "状态"),
        ],
    },
    {
        "class": "SpaceImportBatch",
        "name": "space_import_batch",
        "pk": ("batchId", "batch_id", "Long"),
        "title": "导入批次",
        "base_path": "import-batch",
        "permi": "space:import",
        "fields": [
            ("importType", "import_type", "String", "导入类型"),
            ("fileName", "file_name", "String", "文件名"),
            ("filePath", "file_path", "String", "文件路径"),
            ("totalCount", "total_count", "Integer", "总数"),
            ("successCount", "success_count", "Integer", "成功数"),
            ("failCount", "fail_count", "Integer", "失败数"),
            ("importStatus", "import_status", "String", "导入状态"),
            ("errorMsg", "error_msg", "String", "错误信息"),
        ],
    },
]


def cap(s):
    return s[:1].upper() + s[1:]


def lower_class(cls):
    return cls[:1].lower() + cls[1:]


def all_fields(t):
    fields = [(t["pk"][0], t["pk"][1], t["pk"][2], "主键")] + t["fields"]
    fields += [
        ("createBy", "create_by", "String", "创建者"),
        ("createTime", "create_time", "Date", "创建时间"),
        ("updateBy", "update_by", "String", "更新者"),
        ("updateTime", "update_time", "Date", "更新时间"),
        ("remark", "remark", "String", "备注"),
    ]
    return fields


def write(path, text):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text.replace("\n", "\r\n"), encoding="utf-8")


def gen_domain(t):
    cls = t["class"]
    imports = set(t.get("imports", []))
    imports.add("com.ruoyi.common.core.domain.BaseEntity")
    if any(f[2] == "Date" for f in t["fields"]):
        imports.add("com.fasterxml.jackson.annotation.JsonFormat")
    lines = ["package com.ruoyi.system.domain.space;", ""]
    for imp in sorted(imports):
        lines.append(f"import {imp};")
    lines += ["", f"public class {cls} extends BaseEntity", "{", "    private static final long serialVersionUID = 1L;", ""]
    domain_fields = [(t["pk"][0], t["pk"][1], t["pk"][2], "主键")] + t["fields"]
    for prop, _, typ, comment in domain_fields:
        if typ == "Date":
            lines.append('    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")')
        lines.append(f"    private {typ} {prop};")
    for prop, typ, comment in t.get("extra_fields", []):
        lines.append(f"    private {typ} {prop};")
    lines.append("")
    for prop, _, typ, _ in domain_fields:
        name = cap(prop)
        lines += [
            f"    public {typ} get{name}()",
            "    {",
            f"        return {prop};",
            "    }",
            "",
            f"    public void set{name}({typ} {prop})",
            "    {",
            f"        this.{prop} = {prop};",
            "    }",
            "",
        ]
    for prop, typ, _ in t.get("extra_fields", []):
        name = cap(prop)
        lines += [
            f"    public {typ} get{name}()",
            "    {",
            f"        return {prop};",
            "    }",
            "",
            f"    public void set{name}({typ} {prop})",
            "    {",
            f"        this.{prop} = {prop};",
            "    }",
            "",
        ]
    lines.append("}")
    return "\n".join(lines)


def gen_mapper_interface(t):
    cls = t["class"]
    var = lower_class(cls)
    pk_prop, _, pk_type = t["pk"]
    lines = [
        "package com.ruoyi.system.mapper.space;",
        "",
        "import java.util.List;",
        "import org.apache.ibatis.annotations.Param;",
        f"import com.ruoyi.system.domain.space.{cls};",
        "",
        f"public interface {cls}Mapper",
        "{",
        f"    public {cls} select{cls}ById({pk_type} {pk_prop});",
        f"    public List<{cls}> select{cls}List({cls} {var});",
        f"    public int insert{cls}({cls} {var});",
        f"    public int update{cls}({cls} {var});",
        f"    public int delete{cls}ById({pk_type} {pk_prop});",
        f"    public int delete{cls}ByIds({pk_type}[] {pk_prop}s);",
    ]
    if cls == "SpaceReservationItem":
        lines += [
            "    public List<SpaceReservationItem> selectConflictItems(SpaceReservationItem item);",
            "    public int updateItemStatus(SpaceReservationItem item);",
        ]
    if cls == "SpaceReservation":
        lines += [
            "    public int updateReservationStatus(SpaceReservation reservation);",
        ]
    lines.append("}")
    return "\n".join(lines)


def gen_service_interface(t):
    cls = t["class"]
    var = lower_class(cls)
    pk_prop, _, pk_type = t["pk"]
    lines = [
        "package com.ruoyi.system.service.space;",
        "",
        "import java.util.List;",
        f"import com.ruoyi.system.domain.space.{cls};",
        "",
        f"public interface I{cls}Service",
        "{",
        f"    public {cls} select{cls}ById({pk_type} {pk_prop});",
        f"    public List<{cls}> select{cls}List({cls} {var});",
        f"    public int insert{cls}({cls} {var});",
        f"    public int update{cls}({cls} {var});",
        f"    public int delete{cls}ByIds({pk_type}[] {pk_prop}s);",
        f"    public int delete{cls}ById({pk_type} {pk_prop});",
    ]
    lines.append("}")
    return "\n".join(lines)


def gen_service_impl(t):
    cls = t["class"]
    var = lower_class(cls)
    pk_prop, _, pk_type = t["pk"]
    mapper = var + "Mapper"
    lines = [
        "package com.ruoyi.system.service.space.impl;",
        "",
        "import java.util.List;",
        "import org.springframework.beans.factory.annotation.Autowired;",
        "import org.springframework.stereotype.Service;",
        f"import com.ruoyi.system.domain.space.{cls};",
        f"import com.ruoyi.system.mapper.space.{cls}Mapper;",
        f"import com.ruoyi.system.service.space.I{cls}Service;",
        "",
        "@Service",
        f"public class {cls}ServiceImpl implements I{cls}Service",
        "{",
        "    @Autowired",
        f"    private {cls}Mapper {mapper};",
        "",
        "    @Override",
        f"    public {cls} select{cls}ById({pk_type} {pk_prop})",
        "    {",
        f"        return {mapper}.select{cls}ById({pk_prop});",
        "    }",
        "",
        "    @Override",
        f"    public List<{cls}> select{cls}List({cls} {var})",
        "    {",
        f"        return {mapper}.select{cls}List({var});",
        "    }",
        "",
        "    @Override",
        f"    public int insert{cls}({cls} {var})",
        "    {",
        f"        return {mapper}.insert{cls}({var});",
        "    }",
        "",
        "    @Override",
        f"    public int update{cls}({cls} {var})",
        "    {",
        f"        return {mapper}.update{cls}({var});",
        "    }",
        "",
        "    @Override",
        f"    public int delete{cls}ByIds({pk_type}[] {pk_prop}s)",
        "    {",
        f"        return {mapper}.delete{cls}ByIds({pk_prop}s);",
        "    }",
        "",
        "    @Override",
        f"    public int delete{cls}ById({pk_type} {pk_prop})",
        "    {",
        f"        return {mapper}.delete{cls}ById({pk_prop});",
        "    }",
        "}",
    ]
    return "\n".join(lines)


def gen_xml(t):
    cls = t["class"]
    var = lower_class(cls)
    pk_prop, pk_col, pk_type = t["pk"]
    fields = all_fields(t)
    table = t["name"]
    cols = ", ".join([f[1] for f in fields])
    lines = [
        '<?xml version="1.0" encoding="UTF-8" ?>',
        "<!DOCTYPE mapper",
        'PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"',
        '"http://mybatis.org/dtd/mybatis-3-mapper.dtd">',
        f'<mapper namespace="com.ruoyi.system.mapper.space.{cls}Mapper">',
        "",
        f'    <resultMap type="{cls}" id="{cls}Result">',
        f'        <id property="{pk_prop}" column="{pk_col}" />',
    ]
    for prop, col, _, _ in t["fields"]:
        lines.append(f'        <result property="{prop}" column="{col}" />')
    for prop, col, _, _ in fields[-5:]:
        lines.append(f'        <result property="{prop}" column="{col}" />')
    lines += [
        "    </resultMap>",
        "",
        f'    <sql id="select{cls}Vo">',
        f"        select {cols} from {table}",
        "    </sql>",
        "",
        f'    <select id="select{cls}ById" parameterType="{pk_type}" resultMap="{cls}Result">',
        f"        <include refid=\"select{cls}Vo\"/>",
        f"        where {pk_col} = #{{{pk_prop}}}",
        "    </select>",
        "",
        f'    <select id="select{cls}List" parameterType="{cls}" resultMap="{cls}Result">',
        f"        <include refid=\"select{cls}Vo\"/>",
        "        <where>",
    ]
    if any(f[0] == "delFlag" for f in t["fields"]):
        lines.append("            and del_flag = '0'")
    query_fields = [(t["pk"][0], t["pk"][1], t["pk"][2], "主键")] + t["fields"]
    for prop, col, typ, _ in query_fields:
        if prop == "delFlag":
            continue
        if typ == "String":
            if prop in ("status", "bookable", "reservationType", "itemStatus", "conflictFlag", "ruleType", "importStatus"):
                lines.append(f'            <if test="{prop} != null and {prop} != \'\'"> and {col} = #{{{prop}}}</if>')
            else:
                lines.append(f'            <if test="{prop} != null and {prop} != \'\'"> and {col} like concat(\'%\', #{{{prop}}}, \'%\')</if>')
        else:
            lines.append(f'            <if test="{prop} != null"> and {col} = #{{{prop}}}</if>')
    lines += [
        "        </where>",
        f"        order by {pk_col} desc",
        "    </select>",
        "",
        f'    <insert id="insert{cls}" parameterType="{cls}" useGeneratedKeys="true" keyProperty="{pk_prop}">',
        f"        insert into {table}",
        "        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">",
    ]
    for prop, col, typ, _ in fields:
        if prop == "createTime":
            lines.append(f"            {col},")
        elif prop == "updateTime":
            continue
        else:
            lines.append(f'            <if test="{prop} != null"> {col},</if>')
    lines += [
        "        </trim>",
        "        <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">",
    ]
    for prop, col, typ, _ in fields:
        if prop == "createTime":
            lines.append("            sysdate(),")
        elif prop == "updateTime":
            continue
        else:
            lines.append(f'            <if test="{prop} != null"> #{{{prop}}},</if>')
    lines += [
        "        </trim>",
        "    </insert>",
        "",
        f'    <update id="update{cls}" parameterType="{cls}">',
        f"        update {table}",
        "        <set>",
    ]
    for prop, col, typ, _ in t["fields"]:
        lines.append(f'            <if test="{prop} != null"> {col} = #{{{prop}}},</if>')
    lines += [
        '            <if test="remark != null"> remark = #{remark},</if>',
        '            <if test="updateBy != null"> update_by = #{updateBy},</if>',
        "            update_time = sysdate()",
        "        </set>",
        f"        where {pk_col} = #{{{pk_prop}}}",
        "    </update>",
        "",
    ]
    if any(f[0] == "delFlag" for f in t["fields"]):
        lines += [
            f'    <delete id="delete{cls}ById" parameterType="{pk_type}">',
            f"        update {table} set del_flag = '2' where {pk_col} = #{{{pk_prop}}}",
            "    </delete>",
            "",
            f'    <delete id="delete{cls}ByIds" parameterType="{pk_type}">',
            f"        update {table} set del_flag = '2' where {pk_col} in",
        ]
    else:
        lines += [
            f'    <delete id="delete{cls}ById" parameterType="{pk_type}">',
            f"        delete from {table} where {pk_col} = #{{{pk_prop}}}",
            "    </delete>",
            "",
            f'    <delete id="delete{cls}ByIds" parameterType="{pk_type}">',
            f"        delete from {table} where {pk_col} in",
        ]
    lines += [
        f'        <foreach collection="array" item="{pk_prop}" open="(" separator="," close=")">',
        f"            #{{{pk_prop}}}",
        "        </foreach>",
        "    </delete>",
    ]
    if cls == "SpaceReservationItem":
        lines += [
            "",
            f'    <select id="selectConflictItems" parameterType="{cls}" resultMap="{cls}Result">',
            f"        <include refid=\"select{cls}Vo\"/>",
            "        where room_id = #{roomId}",
            "          and booking_date = #{bookingDate}",
            "          and item_status in ('1', '2')",
            "          and start_time &lt; #{endTime}",
            "          and end_time &gt; #{startTime}",
            "          <if test=\"itemId != null\"> and item_id &lt;&gt; #{itemId}</if>",
            "    </select>",
            "",
            f'    <update id="updateItemStatus" parameterType="{cls}">',
            "        update space_reservation_item",
            "        set item_status = #{itemStatus},",
            "            conflict_flag = #{conflictFlag},",
            "            conflict_reason = #{conflictReason},",
            "            audit_time = sysdate(),",
            "            auditor_id = #{auditorId},",
            "            auditor_name = #{auditorName},",
            "            reject_reason = #{rejectReason},",
            "            update_by = #{updateBy},",
            "            update_time = sysdate()",
            "        where item_id = #{itemId}",
            "    </update>",
        ]
    if cls == "SpaceReservation":
        lines += [
            "",
            f'    <update id="updateReservationStatus" parameterType="{cls}">',
            "        update space_reservation",
            "        set status = #{status},",
            "            auditor_id = #{auditorId},",
            "            auditor_name = #{auditorName},",
            "            audit_time = sysdate(),",
            "            reject_reason = #{rejectReason},",
            "            update_by = #{updateBy},",
            "            update_time = sysdate(),",
            "            version = version + 1",
            "        where reservation_id = #{reservationId}",
            "    </update>",
        ]
    lines += ["", "</mapper>"]
    return "\n".join(lines)


def gen_controller(t):
    cls = t["class"]
    var = lower_class(cls)
    pk_prop, _, pk_type = t["pk"]
    title = t["title"]
    base = t["base_path"]
    permi = t["permi"]
    lines = [
        "package com.ruoyi.web.controller.space;",
        "",
        "import java.util.List;",
        "import org.springframework.beans.factory.annotation.Autowired;",
        "import org.springframework.security.access.prepost.PreAuthorize;",
        "import org.springframework.web.bind.annotation.DeleteMapping;",
        "import org.springframework.web.bind.annotation.GetMapping;",
        "import org.springframework.web.bind.annotation.PathVariable;",
        "import org.springframework.web.bind.annotation.PostMapping;",
        "import org.springframework.web.bind.annotation.PutMapping;",
        "import org.springframework.web.bind.annotation.RequestBody;",
        "import org.springframework.web.bind.annotation.RequestMapping;",
        "import org.springframework.web.bind.annotation.RestController;",
        "import com.ruoyi.common.annotation.Log;",
        "import com.ruoyi.common.core.controller.BaseController;",
        "import com.ruoyi.common.core.domain.AjaxResult;",
        "import com.ruoyi.common.core.page.TableDataInfo;",
        "import com.ruoyi.common.enums.BusinessType;",
        f"import com.ruoyi.system.domain.space.{cls};",
        f"import com.ruoyi.system.service.space.I{cls}Service;",
        "",
        "@RestController",
        f'@RequestMapping("/space/{base}")',
        f"public class {cls}Controller extends BaseController",
        "{",
        "    @Autowired",
        f"    private I{cls}Service {var}Service;",
        "",
        f'    @PreAuthorize("@ss.hasPermi(\'{permi}:list\')")',
        '    @GetMapping("/list")',
        f"    public TableDataInfo list({cls} {var})",
        "    {",
        "        startPage();",
        f"        List<{cls}> list = {var}Service.select{cls}List({var});",
        "        return getDataTable(list);",
        "    }",
        "",
        f'    @PreAuthorize("@ss.hasPermi(\'{permi}:query\')")',
        f'    @GetMapping(value = "/{{{pk_prop}}}")',
        f"    public AjaxResult getInfo(@PathVariable {pk_type} {pk_prop})",
        "    {",
        f"        return success({var}Service.select{cls}ById({pk_prop}));",
        "    }",
        "",
        f'    @PreAuthorize("@ss.hasPermi(\'{permi}:add\')")',
        f'    @Log(title = "{title}", businessType = BusinessType.INSERT)',
        "    @PostMapping",
        f"    public AjaxResult add(@RequestBody {cls} {var})",
        "    {",
        f"        {var}.setCreateBy(getUsername());",
        f"        return toAjax({var}Service.insert{cls}({var}));",
        "    }",
        "",
        f'    @PreAuthorize("@ss.hasPermi(\'{permi}:edit\')")',
        f'    @Log(title = "{title}", businessType = BusinessType.UPDATE)',
        "    @PutMapping",
        f"    public AjaxResult edit(@RequestBody {cls} {var})",
        "    {",
        f"        {var}.setUpdateBy(getUsername());",
        f"        return toAjax({var}Service.update{cls}({var}));",
        "    }",
        "",
        f'    @PreAuthorize("@ss.hasPermi(\'{permi}:remove\')")',
        f'    @Log(title = "{title}", businessType = BusinessType.DELETE)',
        f'    @DeleteMapping("/{{{pk_prop}s}}")',
        f"    public AjaxResult remove(@PathVariable {pk_type}[] {pk_prop}s)",
        "    {",
        f"        return toAjax({var}Service.delete{cls}ByIds({pk_prop}s));",
        "    }",
        "}",
    ]
    return "\n".join(lines)


def gen_api(t):
    cls = t["class"]
    short = cls.replace("Space", "")
    base = t["base_path"]
    pk_prop, _, _ = t["pk"]
    return f"""import request from '@/utils/request'

export function list{short}(query) {{
  return request({{ url: '/space/{base}/list', method: 'get', params: query }})
}}

export function get{short}({pk_prop}) {{
  return request({{ url: '/space/{base}/' + {pk_prop}, method: 'get' }})
}}

export function add{short}(data) {{
  return request({{ url: '/space/{base}', method: 'post', data: data }})
}}

export function update{short}(data) {{
  return request({{ url: '/space/{base}', method: 'put', data: data }})
}}

export function del{short}({pk_prop}) {{
  return request({{ url: '/space/{base}/' + {pk_prop}, method: 'delete' }})
}}
"""


for table in TABLES:
    cls = table["class"]
    write(DOMAIN_DIR / f"{cls}.java", gen_domain(table))
    write(MAPPER_DIR / f"{cls}Mapper.java", gen_mapper_interface(table))
    write(SERVICE_DIR / f"I{cls}Service.java", gen_service_interface(table))
    write(SERVICE_IMPL_DIR / f"{cls}ServiceImpl.java", gen_service_impl(table))
    write(XML_DIR / f"{cls}Mapper.xml", gen_xml(table))
    write(CONTROLLER_DIR / f"{cls}Controller.java", gen_controller(table))
    write(API_DIR / (table["base_path"].replace("/", "-") + ".js"), gen_api(table))

print("Generated space modules")
