package com.kh.youtube.controller;

import com.kh.youtube.domain.*;
import com.kh.youtube.service.CommentLikeService;
import com.kh.youtube.service.VideoCommentService;
import com.kh.youtube.service.VideoLikeService;
import com.kh.youtube.service.VideoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/*")
@Log4j2
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class VideoController {

    @Value("${spring.servlet.multipart.location}")// application.properties에 있는 변수 // 롬복말고 spring꺼임
    private String uploadPath;

    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoLikeService videoLike;
    @Autowired
    private VideoCommentService videoComment;
    @Autowired
    private CommentLikeService commentLike;

    // 영상 전체 조회 GET http://localhost:8080/api/video
    @GetMapping("/video")
    public ResponseEntity <List<Video>> showAllVideo(){
        return ResponseEntity.status(HttpStatus.OK).body(videoService.showAll());
    }
    
    // 영상 추가 POST http://localhost:8080/api/video
    @PostMapping ("/video")
    public ResponseEntity<Video> createVideo(MultipartFile video, MultipartFile image, String title, String desc,String categoryCode){

        log.info("video : " + video);
        log.info("image : " + image);
        log.info("title : " + title);
        log.info("desc : " + desc);
        log.info("categoryCode : " + categoryCode);
        // video_title, video_desc, video_url, video_photo, category_code

        // 업로드 처리
        // 비디오의 실제 파일 이름
        String originalVideo = video.getOriginalFilename();
        // 파일이름에 경로가 같이 붙을 경우가 있어서 방지 차원에서 미리 잘라버림
        String realVideo = originalVideo.substring(originalVideo.lastIndexOf("\\")+1);
        log.info("realVideo : " + realVideo);

        // UUID 랜덤 아이디 부여
        String uuid = UUID.randomUUID().toString();

        // 실제로 저장할 파일 명 (위치 포함)
        String saveVideo = uploadPath + File.separator + uuid + "_" + realVideo;
        Path pathVideo = Paths.get(saveVideo);
        try {
            video.transferTo(pathVideo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 이미지의 실제 파일 이름
        String originalImage = image.getOriginalFilename();
        // 파일이름에 경로가 같이 붙을 경우가 있어서 방지 차원에서 미리 잘라버림
        String realImage = originalImage.substring(originalImage.lastIndexOf("\\")+1);
        String saveImage = uploadPath + File.separator + uuid + "_" + realImage;
        Path pathImage = Paths.get(saveImage);

        try {
            image.transferTo(pathImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 파일 업로드가 끝났으니 경로 (savaPhoto), name, desc, memberId(id) 설정해서 넣는것

        Video vo = new Video();

        vo.setVideoTitle(title);
        vo.setVideoDesc(desc);
        vo.setVideoUrl(saveVideo);
        vo.setVideoPhoto(saveImage);

        Category category = new Category();
        category.setCategoryCode(Integer.parseInt(categoryCode));
        vo.setCategory(category);

        Channel channel = new Channel();
        channel.setChannelCode(21);
        vo.setChannel(channel);

        Member member = new Member();
        member.setId("user1");
        vo.setMember(member);

        //return  ResponseEntity.status(HttpStatus.OK).build();
       return ResponseEntity.status(HttpStatus.OK).body(videoService.create(vo));

    }

    // 영상 수정 PUT http://localhost:8080/api/video
    @PutMapping("/video")
    public ResponseEntity<Video> updateVideo(@RequestBody Video vo){
        return ResponseEntity.status(HttpStatus.OK).body(videoService.update(vo));
    }
    
    // 영상 삭제 DELETE http://localhost:8080/api/video/1 <<-비디오 코드
    @PutMapping("/video/{id}")
    public ResponseEntity<Video> deleteVideo(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(videoService.delete(id));
    }
    // 영상 1개 조회 GET http://localhost:8080/api/video/1
    @GetMapping("/video/{id}")
    public ResponseEntity<Video> showVideo (@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(videoService.show(id));
    }
    
    // 영상 1개에 따른 댓글 전체 조회 GET http://localhost:8080/api/video/1/comment
    @GetMapping("/video/{id}/comment")
    public ResponseEntity <List<VideoComment>> videoCommentList(@PathVariable int code){
        return ResponseEntity.status(HttpStatus.OK).body(videoComment.findByVideoCode(code));
    }

    // 좋아요 추가 (수정,생성) POST http://localhost:8080/api/video/like
    @PostMapping("/video/like")
    public ResponseEntity<VideoLike> createVideoLike(@RequestBody VideoLike vo){
        return ResponseEntity.status(HttpStatus.OK).body(videoLike.create(vo));
    }

    // 좋아요 취소 (삭제) DELETE http://localhost:8080/api/video/like/1
    @DeleteMapping ("/video/like/{code}")
    public ResponseEntity<VideoLike> createVideoLike(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(videoLike.delete(id));
    }


    // 댓글 추가 (수정,생성) POST http://localhost:8080/api/video/comment
    @PostMapping("/video/comment")
    public ResponseEntity<VideoComment> createVideoComment(@RequestBody VideoComment vo){
        return ResponseEntity.status(HttpStatus.OK).body(videoComment.create(vo));
    }

    // 댓글 수정 PUT http://localhost:8080/api/video/comment
    @PutMapping ("/video/comment")
    public ResponseEntity<VideoComment> updateVideoComment(@RequestBody VideoComment vo){
        return ResponseEntity.status(HttpStatus.OK).body(videoComment.update(vo));
    }

    // 댓글 삭제 DELETE http://localhost:8080/api/video/comment/
    @DeleteMapping ("/video/comment")
    public ResponseEntity<VideoComment> deleteVideoComment(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(videoComment.delete(id));
    }


    // 댓글 좋아요 추가 (수정,생성) POST http://localhost:8080/api/video/comment/like
    @PostMapping ("/video/comment/like")
    public ResponseEntity<CommentLike> createCommentLike(@RequestBody CommentLike vo){
        return ResponseEntity.status(HttpStatus.OK).body(commentLike.create(vo));
    }

    // 댓글 좋아요 취소 (삭제) DELETE http://localhost:8080/api/video/comment/like/1
    @DeleteMapping("/video/comment/like/{code}")
    public ResponseEntity<CommentLike> deleteCommentLike(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(commentLike.delete(id));
    }
}

