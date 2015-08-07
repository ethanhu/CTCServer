-- create database
DROP DATABASE IF EXISTS simulatordb; 
CREATE DATABASE simulatordb;
use simulatordb;

-- create a simulatordb user
GRANT RELOAD ON * . * TO 'root'@'localhost'
IDENTIFIED BY '123456'
WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;

-- add select permission to
GRANT SELECT , INSERT , UPDATE , DELETE ON `simulatordb` . * TO 'root'@'localhost';

SET FOREIGN_KEY_CHECKS=0;

-- ѧԱ��
DROP TABLE IF EXISTS `Student`;
CREATE TABLE `Student` (
  `Student_id` int(10) NOT NULL auto_increment COMMENT 'ѧԱѧ��',
  `Student_name` varchar(20) UNIQUE not null COMMENT '�û��˺�',
  `Student_password` varchar(20) default "111111" COMMENT '����',
  `Student_role` varchar(10) default "001002" COMMENT '��ɫ 001002��ʾѧԱ',
  PRIMARY KEY  (`Student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='ѧԱ��';

-- ��ʦ��
DROP TABLE IF EXISTS `Teacher`;
CREATE TABLE `Teacher` (
  `Teacher_id` int(10) NOT NULL auto_increment COMMENT '��ʦ���ID',
  `Teacher_name` varchar(20) unique not null COMMENT '�û��˺�',
  `Teacher_password` varchar(20) default "111111" COMMENT '����',
  `Teacher_role` varchar(10) default "001001" COMMENT '��ɫ 001001��ʾ��ʦ 001005��ʾ����Ա 001006 CTC���е����û�',
  PRIMARY KEY  (`Teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='��ʦ��';

 
-- ��վ��
-- վ��ͼ������xml�ļ����ļ�����Ϊ��վID����վ��ͼ���浽����У�ͬʱ�ṩһ������վ��ͼ�Ĺ��� ��Ԥ�ȶ������ɸ�վ��ͼ ��
DROP TABLE IF EXISTS `Station`;
CREATE TABLE `Station` (
  `Station_name` varchar(20) unique not null COMMENT '��վ���',
  `Station_downnumber` int(4) default 1 COMMENT '��վ���п��ó���',
  `Station_upnumber` int(4) default 1 COMMENT '��վ���п��ó���',
  `Station_graph` varchar(100) default "table" COMMENT 'վ��ͼ table��ʾվ��ͼ�����ڱ��У����򱣴����ļ���',
  PRIMARY KEY  (`Station_name`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='��վ��';


-- �г���
DROP TABLE IF EXISTS `Train`;
CREATE TABLE `Train` (
  `Train_name` varchar(10) not null UNIQUE COMMENT '�������',
  `Train_direction` int(2) default 1 COMMENT '���η�������0������1��',
  `Train_maxspeed` int(5) default 100 COMMENT '������ٶ�',
  `Train_startstationname` varchar(50) default '' COMMENT 'ʼ��վ���',
  `Train_endstationname` varchar(50) default '' COMMENT '����վ���', 
   PRIMARY KEY  (`Train_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='�г���';



-- �г���α� �����Ʊ�ʾ����֣��-����
DROP TABLE IF EXISTS `District`;
CREATE TABLE `District` (
  `District_name` varchar(50) UNIQUE not null COMMENT '������',
  `District_stationnumber` int(4) default 2 COMMENT '���վ����',
  `District_startstationname` varchar(50) NOT NULL default '' COMMENT '��ο�ʼվ���',
  `District_endstationname` varchar(50) NOT NULL default '' COMMENT '��ν���վ���',
  `District_railwaybureau` varchar(50) default '' COMMENT '���������·��',
  PRIMARY KEY  (`District_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='�г���α�';



-- ��������μ�Ĺ�ϵ�� 
-- ��Ӧ��ϵ�ǣ�һ�����ζ�Ӧ�����Σ�һ����ζ�Ӧ�������
DROP TABLE IF EXISTS `TrainDistrictRelation`;
CREATE TABLE `TrainDistrictRelation` (
 `Train_name` varchar(10)  NOT NULL COMMENT '�������',
 `District_name` varchar(50)  NOT NULL COMMENT '������',
  PRIMARY KEY  (`Train_name`,`District_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='��������μ�Ĺ�ϵ ';



-- �г�ԭʼ�ƻ���(�������trainPlan��Ӧ) ������ĳһ����Train_name�������г�վ�������Ϣ 
-- ��Ӧ��ϵ�ǣ���Զ� Լ����������վ��ǰվվ�� = ��վվ��  
--  ����ɾ��ĳ��� ʱ���ֶθ�ֵΪ(Ŀǰ�Ľ������)��00:00:00, ��Ļ�ϲ���ʾ�� ����������һ���ֶΣ�������
DROP TABLE IF EXISTS `Plan`;
CREATE TABLE `Plan` (
  `Train_name` varchar(10)  NOT NULL COMMENT '�������',
  `District_name` varchar(50)  NOT NULL COMMENT '������',
  `Prestation_name` varchar(20) NOT NULL COMMENT 'ǰվվ��',
  `Station_name` varchar(20) NOT NULL COMMENT '��վվ��',
  `Plan_arrivestationtime` time default NULL COMMENT '��վʱ��',
  `Plan_leavestationtime` time default NULL COMMENT '��վʱ��',
  PRIMARY KEY (`Train_name`,`Station_name`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='�г�ԭʼ�ƻ���';

-- ��վ����ι�ϵ��
-- ��Ӧ��ϵ�ǣ�һ����վ��Ӧ�����Σ�һ����ζ�Ӧ�����վ
--  �ֶ�predistance:ͬһ������еı�վ������һվ�ľ���)
DROP TABLE IF EXISTS `StationDistrictRelation`;
CREATE TABLE `StationDistrictRelation` (
  `Station_name` varchar(20) NOT NULL COMMENT '��վ���',
  `Prestation_name` varchar(20) NOT NULL COMMENT 'ǰվվ��',
  `District_name` varchar(50) NOT NULL COMMENT '������',
  `Predistance` int(11) NOT NULL default '0' COMMENT '��վ����һվ�ľ��루���',
  PRIMARY KEY  (`Station_name`,`District_name`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='��վ����ι�ϵ��';


-- �洢�г�����Ա������ԭʼ�ƻ���plan�ĳ��ε���Ϣ 
 --  ����Ӳ������2010-2-1 ����ȡֵ�����������У��г����Ƚ��泵�β��������TDCS_TRAIN_TYPE_NO
DROP TABLE IF EXISTS `Dispatch`;
CREATE TABLE `Dispatch` (
  `Train_name` varchar(10) NOT NULL COMMENT '�������',
  `District_name` varchar(50) NOT NULL COMMENT '������',
  `Prestation_name` varchar(20) NOT NULL COMMENT 'ǰվվ��',
  `Station_name` varchar(20) NOT NULL COMMENT '��վվ��',
  `Dispatch_arrivestationtime` time default NULL COMMENT '��վʱ��',
  `Dispatch_leavestationtime` time default NULL COMMENT '��վʱ��',
  `Train_Type` int(8) default "700001" COMMENT '�������',
  `Operator_name` varchar(20) default NULL COMMENT '����������',
  PRIMARY KEY  (`Train_name`,`Station_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='�г����ȼƻ���';


DROP TABLE IF EXISTS `temptraindistrictrelation`;
CREATE TABLE `temptraindistrictrelation` (
  `Train_name` varchar(10) collate utf8_unicode_ci NOT NULL COMMENT '�������',
  `District_name` varchar(50) collate utf8_unicode_ci NOT NULL COMMENT '������',
  PRIMARY KEY  (`Train_name`,`District_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='��������μ�Ĺ�ϵ(�����ñ�) ';


--��ʱ���α�,����ṹ��trainһ���Ǳ�����ȷ����ʱ��,���train��plan���������Ϣ��ԭʼ�г�������Ϣ��Ҫ����Ա(��ʦ)�����޸ģ�
--��TempTrain��Dispatch�������г�����Ա��train��plan������ӳ��Σ�ɾ��Σ������Ϣ�洢,ÿ�δ洢ǰ,����Ҫ�����2�ű���������
DROP TABLE IF EXISTS `TempTrain`;
CREATE TABLE `TempTrain` (
  `Train_name` varchar(10) collate utf8_unicode_ci NOT NULL COMMENT '�������',
  `Train_direction` int(2) default '1' COMMENT '���η�������0������1��',
  `Train_maxspeed` int(5) default '100' COMMENT '������ٶ�',
  `Train_startstationname` varchar(50) COMMENT 'ʼ��վ���',
  `Train_endstationname` varchar(50) COMMENT '����վ���',
   PRIMARY KEY  (`Train_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='��ʱ�г���';


INSERT INTO `dispatch` VALUES ('1433', '����-���ͺ���', '������', '������', '00:20:00', '00:20:00', 's3');
INSERT INTO `dispatch` VALUES ('1433', '����-���ͺ���', '������', '���ͺ���', '01:55:00', '01:55:00', 's3');
INSERT INTO `dispatch` VALUES ('K125', '����-���ͺ���', '������', '������', '01:00:00', '01:00:00', 's3');
INSERT INTO `dispatch` VALUES ('K125', '����-���ͺ���', '��ͬ', '���ͺ���', '02:45:00', '02:45:00', 's3');
INSERT INTO `dispatch` VALUES ('K125', '����-���ͺ���', '������', '��ͬ', '01:20:00', '01:40:00', 's3');
INSERT INTO `dispatch` VALUES ('S', '����-���ͺ���', '���ͺ���', '�����첼', '01:03:00', '01:12:00', 's3');
INSERT INTO `dispatch` VALUES ('S', '����-���ͺ���', '�żҿ�', '������', '02:33:00', '02:33:00', 's3');
INSERT INTO `dispatch` VALUES ('S', '����-���ͺ���', '���ͺ���', '���ͺ���', '00:41:00', '00:41:00', 's3');
INSERT INTO `dispatch` VALUES ('S', '����-���ͺ���', '�����첼', '��ͬ', '01:31:00', '01:37:00', 's3');
INSERT INTO `dispatch` VALUES ('S', '����-���ͺ���', '��ͬ', '�żҿ�', '01:59:00', '02:15:00', 's3');
INSERT INTO `dispatch` VALUES ('T281', '����-���ͺ���', '��ͬ', '�����첼', '02:55:00', '03:05:00', 's3');
INSERT INTO `dispatch` VALUES ('T281', '����-���ͺ���', '������', '������', '01:15:00', '01:15:00', 's3');
INSERT INTO `dispatch` VALUES ('T281', '����-���ͺ���', '�����첼', '���ͺ���', '03:30:00', '03:30:00', 's3');
INSERT INTO `dispatch` VALUES ('T281', '����-���ͺ���', '�żҿ�', '��ͬ', '02:36:00', '02:46:00', 's3');
INSERT INTO `dispatch` VALUES ('T281', '����-���ͺ���', '������', '�żҿ�', '01:50:00', '02:00:00', 's3');
INSERT INTO `dispatch` VALUES ('X', '����-���ͺ���', '�żҿ�', '�����첼', '00:39:00', '00:49:00', 's3');
INSERT INTO `dispatch` VALUES ('X', '����-���ͺ���', '������', '������', '00:07:00', '00:07:00', 's3');
INSERT INTO `dispatch` VALUES ('X', '����-���ͺ���', '�����첼', '���ͺ���', '00:59:00', '00:59:00', 's3');
INSERT INTO `dispatch` VALUES ('X', '����-���ͺ���', '������', '�żҿ�', '00:17:00', '00:24:00', 's3');
INSERT INTO `district` VALUES ('����-���ͺ���', '5', '������', '���ͺ���', '���ͺ�����·��');
INSERT INTO `district` VALUES ('���ͺ���-��ͬ', '3', '���ͺ���', '��ͬ', '���ͺ�����·��');
INSERT INTO `plan` VALUES ('1433', '����-���ͺ���', '������', '������', '00:20:00', '00:20:00');
INSERT INTO `plan` VALUES ('1433', '����-���ͺ���', '������', '���ͺ���', '01:55:00', '01:55:00');
INSERT INTO `plan` VALUES ('K125', '����-���ͺ���', '������', '������', '01:00:00', '01:00:00');
INSERT INTO `plan` VALUES ('K125', '����-���ͺ���', '��ͬ', '���ͺ���', '02:45:00', '02:45:00');
INSERT INTO `plan` VALUES ('K125', '����-���ͺ���', '������', '��ͬ', '01:20:00', '01:40:00');
INSERT INTO `plan` VALUES ('T11', '���ͺ���-��ͬ', '���ͺ���', '���ͺ���', '00:20:00', '00:20:00');
INSERT INTO `plan` VALUES ('T11', '���ͺ���-��ͬ', '����', '��ͬ', '01:35:00', '01:35:00');
INSERT INTO `plan` VALUES ('T11', '���ͺ���-��ͬ', '���ͺ���', '����', '00:50:00', '01:00:00');
INSERT INTO `plan` VALUES ('T281', '����-���ͺ���', '��ͬ', '�����첼', '02:55:00', '03:05:00');
INSERT INTO `plan` VALUES ('T281', '����-���ͺ���', '������', '������', '01:15:00', '01:15:00');
INSERT INTO `plan` VALUES ('T281', '����-���ͺ���', '�����첼', '���ͺ���', '03:30:00', '03:30:00');
INSERT INTO `plan` VALUES ('T281', '����-���ͺ���', '�żҿ�', '��ͬ', '02:36:00', '02:46:00');
INSERT INTO `plan` VALUES ('T281', '����-���ͺ���', '������', '�żҿ�', '01:50:00', '02:00:00');
INSERT INTO `station` VALUES ('�����첼', '2', '2', 'table');
INSERT INTO `station` VALUES ('������', '5', '4', 'table');
INSERT INTO `station` VALUES ('���ͺ���', '4', '3', 'table');
INSERT INTO `station` VALUES ('��ͬ', '2', '3', 'table');
INSERT INTO `station` VALUES ('�żҿ�', '2', '2', 'table');
INSERT INTO `station` VALUES ('����', '2', '2', 'table');
INSERT INTO `stationdistrictrelation` VALUES ('�����첼', '��ͬ', '����-���ͺ���', '150');
INSERT INTO `stationdistrictrelation` VALUES ('������', '������', '����-���ͺ���', '0');
INSERT INTO `stationdistrictrelation` VALUES ('���ͺ���', '�����첼', '����-���ͺ���', '130');
INSERT INTO `stationdistrictrelation` VALUES ('���ͺ���', '���ͺ���', '���ͺ���-��ͬ', '0');
INSERT INTO `stationdistrictrelation` VALUES ('��ͬ', '�żҿ�', '����-���ͺ���', '180');
INSERT INTO `stationdistrictrelation` VALUES ('��ͬ', '����', '���ͺ���-��ͬ', '300');
INSERT INTO `stationdistrictrelation` VALUES ('�żҿ�', '������', '����-���ͺ���', '200');
INSERT INTO `stationdistrictrelation` VALUES ('����', '���ͺ���', '���ͺ���-��ͬ', '240');

INSERT INTO `student` VALUES ('2', 's1', '1', '001002');
INSERT INTO `student` VALUES ('3', 's2', '1', '001002');
INSERT INTO `student` VALUES ('4', 's3', '1', '001002');
INSERT INTO `student` VALUES ('5', 's4', '1', '001002');
INSERT INTO `student` VALUES ('6', 's5', '1', '001002');
INSERT INTO `student` VALUES ('7', 's6', '1', '001002');
INSERT INTO `student` VALUES ('8', 's7', '1', '001002');
INSERT INTO `student` VALUES ('9', 's8', '1', '001002');
INSERT INTO `student` VALUES ('10', 's9', '1', '001002');
INSERT INTO `student` VALUES ('8120487', '��ѫ', '3', '001002');
INSERT INTO `student` VALUES ('9120548', '�̽�', '2', '001002');
INSERT INTO `student` VALUES ('9120556', '�����', '1', '001002');

INSERT INTO `teacher` VALUES ('1', 'ctc', 'ctc', '001006');
INSERT INTO `teacher` VALUES ('2', 'admin', 'admin', '001005');
INSERT INTO `teacher` VALUES ('3', 'user1', '1', '001001');
INSERT INTO `teacher` VALUES ('4', 'user2', '2', '001001');


INSERT INTO `temptrain` VALUES ('1433', '1', '100', null, null);
INSERT INTO `temptrain` VALUES ('K125', '1', '100', null, null);
INSERT INTO `temptrain` VALUES ('S', '0', '100', null, null);
INSERT INTO `temptrain` VALUES ('T281', '1', '100', null, null);
INSERT INTO `temptrain` VALUES ('X', '1', '100', null, null);
INSERT INTO `temptraindistrictrelation` VALUES ('1433', '����-���ͺ���');
INSERT INTO `temptraindistrictrelation` VALUES ('K125', '����-���ͺ���');
INSERT INTO `temptraindistrictrelation` VALUES ('S', '����-���ͺ���');
INSERT INTO `temptraindistrictrelation` VALUES ('T281', '����-���ͺ���');
INSERT INTO `temptraindistrictrelation` VALUES ('X', '����-���ͺ���');
INSERT INTO `train` VALUES ('1433', '1', '120', '������', '���ͺ���');
INSERT INTO `train` VALUES ('K125', '1', '150', '������', '���ͺ���');
INSERT INTO `train` VALUES ('T11', '1', '100', '���ͺ���', '��ͬ');
INSERT INTO `train` VALUES ('T281', '1', '200', '������', '���ͺ���');
INSERT INTO `traindistrictrelation` VALUES ('1433', '����-���ͺ���');
INSERT INTO `traindistrictrelation` VALUES ('K125', '����-���ͺ���');
INSERT INTO `traindistrictrelation` VALUES ('T11', '���ͺ���-��ͬ');
INSERT INTO `traindistrictrelation` VALUES ('T281', '����-���ͺ���');

