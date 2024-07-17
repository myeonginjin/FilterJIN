## 📸  2D LUT(64^3)기반 이미지 프로세싱 App



### LUT를 이용한 8가지의 이미지 필터와 RGBA의 배합을 통한 그레이스케일 필터 3개로 이루어진</br>이미지 프로세싱 App입니다.
* 📝  **제작 내용** :
   * Input 이미지 각 픽셀의 RGBA값을 통해 1차원 LUT.png파일로 변한된 **LUT큐브 인덱싱**
   * 기존 256^3에서 64^3으로 축소된 큐브를 보완하기 위해 **이미지 선형보간**(1D Linear Interpolation)적용
   * 퍼포먼스 향상을 위해 **이미지 다운사이징**을 통한 프리뷰
   * 프로그래스바를 이용한 **비동기처리** 활용

*   **개발자** : 진명인

* **기술스택** :
  * Android Studio
  * Aos
  * Kotlin   </br>

* **스크린샷** :  </br>


&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![스크린샷 2024-07-17 오후 1 48 55 (3) (1)](https://github.com/user-attachments/assets/644adc02-9e7a-4000-90bd-f74e6302cf19)

