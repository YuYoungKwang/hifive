package com.app.dao.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.dao.PlaceDAO;
import com.app.dto.PlaceDTO;
import com.app.dto.PlaceTagMapDTO;

@Repository
public class PlaceDAOImpl implements PlaceDAO {

	private static final String STATEMENT_ID = "place_mapper.updateAreaBasedListPlaces";
	private static final String NEXT_PLACE_NO_STATEMENT_ID = "place_mapper.getNextPlaceNo";
	private static final String INSERT_PLACE_TAG_MAP_STATEMENT_ID = "place_mapper.insertPlaceTagMap";
	private static final String DELETE_ALL_PLACE_TAG_MAP_STATEMENT_ID = "place_mapper.deleteAllPlaceTagMap";
	private static final String DELETE_ALL_PLACE_STATEMENT_ID = "place_mapper.deleteAllPlace";
	private static final String DROP_SEQ_PLACE_NO_STATEMENT_ID = "place_mapper.dropSeqPlaceNo";
	private static final String CREATE_SEQ_PLACE_NO_STATEMENT_ID = "place_mapper.createSeqPlaceNo";
	private static final String DROP_SEQ_PLACE_TAG_MAP_NO_STATEMENT_ID = "place_mapper.dropSeqPlaceTagMapNo";
	private static final String CREATE_SEQ_PLACE_TAG_MAP_NO_STATEMENT_ID = "place_mapper.createSeqPlaceTagMapNo";

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Override
	public void resetPlaceImportData() throws Exception {
		sqlSessionTemplate.delete(DELETE_ALL_PLACE_TAG_MAP_STATEMENT_ID);
		sqlSessionTemplate.delete(DELETE_ALL_PLACE_STATEMENT_ID);
		sqlSessionTemplate.update(DROP_SEQ_PLACE_TAG_MAP_NO_STATEMENT_ID);
		sqlSessionTemplate.update(CREATE_SEQ_PLACE_TAG_MAP_NO_STATEMENT_ID);
		sqlSessionTemplate.update(DROP_SEQ_PLACE_NO_STATEMENT_ID);
		sqlSessionTemplate.update(CREATE_SEQ_PLACE_NO_STATEMENT_ID);
	}

	@Override
	public Long getNextPlaceNo() throws Exception {
		return sqlSessionTemplate.selectOne(NEXT_PLACE_NO_STATEMENT_ID);
	}

	@Override
	public int updateAreaBasedListPlaces(PlaceDTO placeDTO) throws Exception {
		return sqlSessionTemplate.insert(STATEMENT_ID, placeDTO);
	}

	@Override
	public int updateAreaBasedListPlacesBatch(List<PlaceDTO> placeDTOList) throws Exception {
		if (placeDTOList == null || placeDTOList.isEmpty()) {
			return 0;
		}

		SqlSession batchSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
		try {
			for (PlaceDTO placeDTO : placeDTOList) {
				batchSession.insert(STATEMENT_ID, placeDTO);
			}
			batchSession.commit();
			return placeDTOList.size();
		} catch (Exception e) {
			batchSession.rollback();
			throw e;
		} finally {
			batchSession.close();
		}
	}

	@Override
	public int insertPlaceTagMapBatch(List<PlaceTagMapDTO> placeTagMapDTOList) throws Exception {
		if (placeTagMapDTOList == null || placeTagMapDTOList.isEmpty()) {
			return 0;
		}

		SqlSession batchSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
		try {
			for (PlaceTagMapDTO placeTagMapDTO : placeTagMapDTOList) {
				batchSession.insert(INSERT_PLACE_TAG_MAP_STATEMENT_ID, placeTagMapDTO);
			}
			batchSession.commit();
			return placeTagMapDTOList.size();
		} catch (Exception e) {
			batchSession.rollback();
			throw e;
		} finally {
			batchSession.close();
		}
	}
}
