package com.app.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.dao.PlaceDAO;
import com.app.dto.PlaceDTO;
import com.app.dto.PlaceHoursDTO;
import com.app.dto.PlaceReviewDTO;
import com.app.dto.PlaceTagMapDTO;

@Repository
public class PlaceDAOImpl implements PlaceDAO {

	private static final String INSERT_PLACE_STATEMENT_ID = "place_mapper.updateAreaBasedListPlaces";
	private static final String NEXT_PLACE_NO_STATEMENT_ID = "place_mapper.getNextPlaceNo";
	private static final String NEXT_PLACE_REVIEW_COMMENT_NO_STATEMENT_ID = "place_mapper.getNextPlaceReviewCommentNo";
	private static final String INSERT_PLACE_TAG_MAP_STATEMENT_ID = "place_mapper.insertPlaceTagMap";
	private static final String INSERT_PLACE_HOURS_STATEMENT_ID = "place_mapper.insertPlaceHours";
	private static final String INSERT_PLACE_REVIEW_STATEMENT_ID = "place_mapper.insertPlaceReview";
	private static final String UPDATE_PLACE_REVIEW_BY_OWNER_STATEMENT_ID = "place_mapper.updatePlaceReviewByOwner";
	private static final String DELETE_PLACE_REVIEW_PHOTOS_BY_OWNER_STATEMENT_ID = "place_mapper.deletePlaceReviewPhotosByOwner";
	private static final String DELETE_PLACE_REVIEW_BY_OWNER_STATEMENT_ID = "place_mapper.deletePlaceReviewByOwner";
	private static final String UPDATE_PLACE_RATING_BY_PLACE_NO_STATEMENT_ID = "place_mapper.updatePlaceRatingByPlaceNo";
	private static final String SELECT_PLACES_FOR_HOURS_IMPORT_STATEMENT_ID = "place_mapper.selectPlacesForHoursImport";
	private static final String DELETE_ALL_PHOTO_DATA_STATEMENT_ID = "place_mapper.deleteAllPhotoData";
	private static final String DELETE_ALL_PLACE_REVIEW_STATEMENT_ID = "place_mapper.deleteAllPlaceReview";
	private static final String DELETE_ALL_TRAVEL_LOGS_STATEMENT_ID = "place_mapper.deleteAllTravelLogs";
	private static final String DELETE_ALL_PLAN_DETAILS_STATEMENT_ID = "place_mapper.deleteAllPlanDetails";
	private static final String DELETE_ALL_WISHLIST_STATEMENT_ID = "place_mapper.deleteAllWishlist";
	private static final String DELETE_ALL_PLACE_HOURS_STATEMENT_ID = "place_mapper.deleteAllPlaceHours";
	private static final String DELETE_ALL_PLACE_TAG_MAP_STATEMENT_ID = "place_mapper.deleteAllPlaceTagMap";
	private static final String DELETE_ALL_PLACE_STATEMENT_ID = "place_mapper.deleteAllPlace";
	private static final String DROP_SEQ_PLACE_NO_STATEMENT_ID = "place_mapper.dropSeqPlaceNo";
	private static final String CREATE_SEQ_PLACE_NO_STATEMENT_ID = "place_mapper.createSeqPlaceNo";
	private static final String DROP_SEQ_PLACE_TAG_MAP_NO_STATEMENT_ID = "place_mapper.dropSeqPlaceTagMapNo";
	private static final String CREATE_SEQ_PLACE_TAG_MAP_NO_STATEMENT_ID = "place_mapper.createSeqPlaceTagMapNo";
	private static final String SELECT_PLACE_BY_PLACE_NO_STATEMENT_ID = "place_mapper.selectPlaceByPlaceNo";
	private static final String SELECT_PLACE_BY_CONTENT_ID_STATEMENT_ID = "place_mapper.selectPlaceByContentId";
	private static final String SELECT_PLACE_BY_NAME_ADDRESS_STATEMENT_ID = "place_mapper.selectPlaceByNameAddress";
	private static final String SELECT_PLACE_BY_NAME_NEAR_LAT_LNG_STATEMENT_ID = "place_mapper.selectPlaceByNameNearLatLng";
	private static final String SELECT_PLACE_TAG_NAMES_BY_PLACE_NO_STATEMENT_ID = "place_mapper.selectPlaceTagNamesByPlaceNo";
	private static final String SELECT_PLACE_PHOTO_URLS_BY_PLACE_NO_STATEMENT_ID = "place_mapper.selectPlacePhotoUrlsByPlaceNo";
	private static final String SELECT_PLACE_REVIEWS_BY_PLACE_NO_STATEMENT_ID = "place_mapper.selectPlaceReviewsByPlaceNo";
	private static final String COUNT_PLACE_REVIEWS_BY_CREATED_BY_STATEMENT_ID = "place_mapper.countPlaceReviewsByCreatedBy";
	private static final String SELECT_PLACE_REVIEWS_BY_CREATED_BY_PAGED_STATEMENT_ID = "place_mapper.selectPlaceReviewsByCreatedByPaged";
	private static final String SELECT_PLACE_REVIEWS_BY_CREATED_BY_PAGED_SORTED_STATEMENT_ID = "place_mapper.selectPlaceReviewsByCreatedByPagedSorted";
	private static final String SELECT_PLACE_HOURS_BY_PLACE_NO_STATEMENT_ID = "place_mapper.selectPlaceHoursByPlaceNo";
	private static final String DROP_SEQ_HOURS_ID_STATEMENT_ID = "place_mapper.dropSeqHoursId";
	private static final String CREATE_SEQ_HOURS_ID_STATEMENT_ID = "place_mapper.createSeqHoursId";

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Override
	public void resetPlaceImportData() throws Exception {
		sqlSessionTemplate.delete(DELETE_ALL_PHOTO_DATA_STATEMENT_ID);
		sqlSessionTemplate.delete(DELETE_ALL_PLACE_REVIEW_STATEMENT_ID);
		sqlSessionTemplate.delete(DELETE_ALL_TRAVEL_LOGS_STATEMENT_ID);
		sqlSessionTemplate.delete(DELETE_ALL_PLAN_DETAILS_STATEMENT_ID);
		sqlSessionTemplate.delete(DELETE_ALL_WISHLIST_STATEMENT_ID);
		sqlSessionTemplate.delete(DELETE_ALL_PLACE_TAG_MAP_STATEMENT_ID);
		sqlSessionTemplate.delete(DELETE_ALL_PLACE_HOURS_STATEMENT_ID);
		sqlSessionTemplate.delete(DELETE_ALL_PLACE_STATEMENT_ID);
		sqlSessionTemplate.update(DROP_SEQ_PLACE_TAG_MAP_NO_STATEMENT_ID);
		sqlSessionTemplate.update(CREATE_SEQ_PLACE_TAG_MAP_NO_STATEMENT_ID);
		sqlSessionTemplate.update(DROP_SEQ_HOURS_ID_STATEMENT_ID);
		sqlSessionTemplate.update(CREATE_SEQ_HOURS_ID_STATEMENT_ID);
		sqlSessionTemplate.update(DROP_SEQ_PLACE_NO_STATEMENT_ID);
		sqlSessionTemplate.update(CREATE_SEQ_PLACE_NO_STATEMENT_ID);
	}

	@Override
	public void resetPlaceHoursImportData() throws Exception {
		sqlSessionTemplate.delete(DELETE_ALL_PLACE_HOURS_STATEMENT_ID);
		sqlSessionTemplate.update(DROP_SEQ_HOURS_ID_STATEMENT_ID);
		sqlSessionTemplate.update(CREATE_SEQ_HOURS_ID_STATEMENT_ID);
	}

	@Override
	public Long getNextPlaceNo() throws Exception {
		return sqlSessionTemplate.selectOne(NEXT_PLACE_NO_STATEMENT_ID);
	}

	@Override
	public int updateAreaBasedListPlaces(PlaceDTO placeDTO) throws Exception {
		return sqlSessionTemplate.insert(INSERT_PLACE_STATEMENT_ID, placeDTO);
	}

	@Override
	public int updateAreaBasedListPlacesBatch(List<PlaceDTO> placeDTOList) throws Exception {
		return executeBatchInsert(placeDTOList, (batchSession, placeDTO) -> batchSession.insert(INSERT_PLACE_STATEMENT_ID, placeDTO));
	}

	@Override
	public int insertPlaceTagMapBatch(List<PlaceTagMapDTO> placeTagMapDTOList) throws Exception {
		return executeBatchInsert(placeTagMapDTOList,
				(batchSession, placeTagMapDTO) -> batchSession.insert(INSERT_PLACE_TAG_MAP_STATEMENT_ID, placeTagMapDTO));
	}

	@Override
	public PlaceDTO selectPlaceByPlaceNo(Long placeNo) throws Exception {
		return sqlSessionTemplate.selectOne(SELECT_PLACE_BY_PLACE_NO_STATEMENT_ID, placeNo);
	}

	@Override
	public PlaceDTO selectPlaceByContentId(String placeContentId) throws Exception {
		return sqlSessionTemplate.selectOne(SELECT_PLACE_BY_CONTENT_ID_STATEMENT_ID, placeContentId);
	}

	@Override
	public PlaceDTO selectPlaceByNameAddress(String placeName, String placeAddress) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("placeName", placeName);
		params.put("placeAddress", placeAddress);
		return sqlSessionTemplate.selectOne(SELECT_PLACE_BY_NAME_ADDRESS_STATEMENT_ID, params);
	}

	@Override
	public PlaceDTO selectPlaceByNameNearLatLng(String placeName, Double placeLatitude, Double placeLongitude) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("placeName", placeName);
		params.put("placeLatitude", placeLatitude);
		params.put("placeLongitude", placeLongitude);
		return sqlSessionTemplate.selectOne(SELECT_PLACE_BY_NAME_NEAR_LAT_LNG_STATEMENT_ID, params);
	}

	@Override
	public List<String> selectPlaceTagNamesByPlaceNo(Long placeNo) throws Exception {
		return sqlSessionTemplate.selectList(SELECT_PLACE_TAG_NAMES_BY_PLACE_NO_STATEMENT_ID, placeNo);
	}

	@Override
	public List<String> selectPlacePhotoUrlsByPlaceNo(Long placeNo) throws Exception {
		return sqlSessionTemplate.selectList(SELECT_PLACE_PHOTO_URLS_BY_PLACE_NO_STATEMENT_ID, placeNo);
	}

	@Override
	public List<PlaceReviewDTO> selectPlaceReviewsByPlaceNo(Long placeNo) throws Exception {
		return sqlSessionTemplate.selectList(SELECT_PLACE_REVIEWS_BY_PLACE_NO_STATEMENT_ID, placeNo);
	}

	@Override
	public int countPlaceReviewsByCreatedBy(String createdBy) throws Exception {
		return toCount(sqlSessionTemplate.selectOne(COUNT_PLACE_REVIEWS_BY_CREATED_BY_STATEMENT_ID, createdBy));
	}

	@Override
	public List<PlaceReviewDTO> selectPlaceReviewsByCreatedByPaged(String createdBy, int startRow, int endRow) throws Exception {
		return sqlSessionTemplate.selectList(SELECT_PLACE_REVIEWS_BY_CREATED_BY_PAGED_STATEMENT_ID,
				buildReviewPageParams(createdBy, startRow, endRow));
	}

	@Override
	public List<PlaceReviewDTO> selectPlaceReviewsByCreatedByPaged(String createdBy, int startRow, int endRow, String sortType)
			throws Exception {
		Map<String, Object> params = buildReviewPageParams(createdBy, startRow, endRow);
		params.put("sortType", sortType);
		return sqlSessionTemplate.selectList(SELECT_PLACE_REVIEWS_BY_CREATED_BY_PAGED_SORTED_STATEMENT_ID, params);
	}

	@Override
	public List<PlaceHoursDTO> selectPlaceHoursByPlaceNo(Long placeNo) throws Exception {
		return sqlSessionTemplate.selectList(SELECT_PLACE_HOURS_BY_PLACE_NO_STATEMENT_ID, placeNo);
	}

	@Override
	public Long getNextPlaceReviewCommentNo() throws Exception {
		return sqlSessionTemplate.selectOne(NEXT_PLACE_REVIEW_COMMENT_NO_STATEMENT_ID);
	}

	@Override
	public int insertPlaceReview(PlaceReviewDTO placeReviewDTO) throws Exception {
		return sqlSessionTemplate.insert(INSERT_PLACE_REVIEW_STATEMENT_ID, placeReviewDTO);
	}

	@Override
	public int updatePlaceReviewByOwner(PlaceReviewDTO placeReviewDTO) throws Exception {
		return sqlSessionTemplate.update(UPDATE_PLACE_REVIEW_BY_OWNER_STATEMENT_ID, placeReviewDTO);
	}

	@Override
	public int deletePlaceReviewByOwner(Long commentNo, Long placeNo, String createdBy) throws Exception {
		Map<String, Object> params = buildReviewOwnerParams(commentNo, placeNo, createdBy);

		sqlSessionTemplate.delete(DELETE_PLACE_REVIEW_PHOTOS_BY_OWNER_STATEMENT_ID, params);
		return sqlSessionTemplate.delete(DELETE_PLACE_REVIEW_BY_OWNER_STATEMENT_ID, params);
	}

	@Override
	public int updatePlaceRatingByPlaceNo(Long placeNo) throws Exception {
		return sqlSessionTemplate.update(UPDATE_PLACE_RATING_BY_PLACE_NO_STATEMENT_ID, placeNo);
	}

	@Override
	public int insertPlaceHoursBatch(List<PlaceHoursDTO> placeHoursDTOList) throws Exception {
		return executeBatchInsert(placeHoursDTOList,
				(batchSession, placeHoursDTO) -> batchSession.insert(INSERT_PLACE_HOURS_STATEMENT_ID, placeHoursDTO));
	}

	@Override
	public List<PlaceDTO> selectPlacesForHoursImport() throws Exception {
		return sqlSessionTemplate.selectList(SELECT_PLACES_FOR_HOURS_IMPORT_STATEMENT_ID);
	}

	private int toCount(Integer count) {
		return count == null ? 0 : count;
	}

	private Map<String, Object> buildReviewPageParams(String createdBy, int startRow, int endRow) {
		Map<String, Object> params = new HashMap<>();
		params.put("createdBy", createdBy);
		params.put("startRow", startRow);
		params.put("endRow", endRow);
		return params;
	}

	private Map<String, Object> buildReviewOwnerParams(Long commentNo, Long placeNo, String createdBy) {
		Map<String, Object> params = new HashMap<>();
		params.put("commentNo", commentNo);
		params.put("placeNo", placeNo);
		params.put("createdBy", createdBy);
		return params;
	}

	private <T> int executeBatchInsert(List<T> items, BiConsumer<SqlSession, T> insertAction) throws Exception {
		if (items == null || items.isEmpty()) {
			return 0;
		}

		SqlSession batchSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
		try {
			for (T item : items) {
				insertAction.accept(batchSession, item);
			}
			batchSession.commit();
			return items.size();
		} catch (Exception e) {
			batchSession.rollback();
			throw e;
		} finally {
			batchSession.close();
		}
	}
}
