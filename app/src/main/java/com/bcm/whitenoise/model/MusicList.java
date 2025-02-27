package com.bcm.whitenoise.model;

import com.bcm.whitenoise.constant.MusicConst;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MusicList {

    //region 변수
    private static MusicList instance;
    public ArrayList<MusicVO> arrMusicList = new ArrayList<>();

    public ArrayList<MusicVO> arrFilteredMusicList = new ArrayList<>();
    //endregion

    private MusicList(){
        makeMusicList();
    }

    public static MusicList getInstance() {
        if(instance == null) {
            instance = new MusicList();
        }
        return instance;
    }

    // 음악정보 배열생성 (여기에서 앱에 표시될 음악을 추가/삭제)
    private void makeMusicList() {
        //함수명,제목,작곡가,음악 총 재생시간,음악 파일명,이미지 파일명,gif 이미지 파일명
        addMusicList(MusicConst.type.Meditation.name(),"우산에 부딧히는 빗소리","빗소리","30","no_1_emotional_piano_music","img_list_1","gif_no_1");
        addMusicList(MusicConst.type.Goodnight.name(),"무라카미T 내가 사랑한 티셔츠","무라카미 하루키","112","no_2_summer_memories","img_list_2","gif_no_2");
        addMusicList(MusicConst.type.Sing.name(),"색체가 없는 다자키 쓰쿠루와 그가 순례를 떠난 해","무라카미 하루키","195","no_3_lost_in_dreams","img_list_3","gif_no_3");
        addMusicList(MusicConst.type.Meditation.name(),"세상에 빛나지 않는 별은 없어","김미라","96","no_4_stylish_deep_electronic","img_list_4","gif_no_4");
        addMusicList(MusicConst.type.Meditation.name(),"중국행 슬로보트","무라카미 하루키","90","no_5_motivational_background","img_list_5","gif_no_5");
        addMusicList(MusicConst.type.Meditation.name(),"무라카미 하루키 잡문집","무라카미 하루키","90","no_5_motivational_background","img_list_5","gif_no_5");

        //함수명,제목,작곡가,음악 총 재생시간,음악 파일명,이미지 파일명,gif 이미지 파일명,
        addMusicList(MusicConst.type.Meditation.name(),"바보빅터","호야킴 데 포사다","133","no_1_emotional_piano_music","img_list_1","gif_no_1");
        addMusicList(MusicConst.type.Goodnight.name(),"무라카미T 내가 사랑한 티셔츠","무라카미 하루키","112","no_2_summer_memories","img_list_2","gif_no_2");
        addMusicList(MusicConst.type.Sing.name(),"색체가 없는 다자키 쓰쿠루와 그가 순례를 떠난 해","무라카미 하루키","195","no_3_lost_in_dreams","img_list_3","gif_no_3");
        addMusicList(MusicConst.type.Meditation.name(),"세상에 빛나지 않는 별은 없어","김미라","96","no_4_stylish_deep_electronic","img_list_4","gif_no_4");
        addMusicList(MusicConst.type.Meditation.name(),"중국행 슬로보트","무라카미 하루키","90","no_5_motivational_background","img_list_5","gif_no_5");
        addMusicList(MusicConst.type.Meditation.name(),"무라카미 하루키 잡문집","무라카미 하루키","90","no_5_motivational_background","img_list_5","gif_no_5");

        // 목록 다수 표시를 위해 중복해서 추가
        addMusicList(MusicConst.type.Meditation.name(), "Emotional Piano MusicEmotional Piano\n\n\uD83E\uDDBEMusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano MusicEmotional Piano Music","SigmaMusicArtSigmaMusicArtSigmaMusicArtSigmaMusicArtSigmaMusicArtSigmaMusicArt","133","no_1_emotional_piano_music","img_list_1", "gif_no_1");
        addMusicList(MusicConst.type.Goodnight.name(), "Summer Memories","Top-Flow","112","no_2_summer_memories","img_list_2", "gif_no_2");
        addMusicList(MusicConst.type.Sing.name(), "Lost in Dreams","Kulakovka","195","no_3_lost_in_dreams","img_list_3", "gif_no_3");
        addMusicList(MusicConst.type.Meditation.name(), "Stylish Deep Electronic","RoyaltyFreeMusic","96","no_4_stylish_deep_electronic","img_list_4", "gif_no_4");
        addMusicList(MusicConst.type.Meditation.name(), "Motivational","DayNightMorning","90","no_5_motivational_background","img_list_5", "gif_no_5");

        arrFilteredMusicList = arrMusicList;
    }

    /**
     * 배열에 음악정보 추가
     * @param _type 음악 타입
     * @param _title 음악 제목
     * @param _composer 음악 작곡가
     * @param _playTimeSec 음악 플레이시간(초)
     * @param _musicFileName 음악 파일명
     * @param _imgFileName 음악 커버 이미지 파일명
     * @param _bgFileName 음악 gif 이미지 파일명
     */
    private void addMusicList(String _type, String _title, String _composer, String _playTimeSec, String _musicFileName, String _imgFileName, String _bgFileName) {
        MusicVO vo = new MusicVO(_type, _title, _composer, _playTimeSec, _musicFileName, _imgFileName, _bgFileName);
        arrMusicList.add(vo);
    }

    // 음악 타입에 해당하는 목록 필터
    public void filterMusicList(String _type) {
        if (_type.equals(MusicConst.type.All.name())) {
            arrFilteredMusicList = arrMusicList;
        } else
            arrFilteredMusicList = (ArrayList<MusicVO>) arrMusicList.stream().filter(music -> music.getType().equals(_type)).collect(Collectors.toList());
    }
}
