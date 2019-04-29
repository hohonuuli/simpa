function spatialLocationTest2

% Each matrix contains info for [refImage47 refImage48 refImage49]

i = [108:118];                                     % Tile Index
w = [3.4298 3.4444 3.4298 3.4928 3.3765 3.3136 3.2167 3.2167 3.1925 ...
    3.1149 3.1101];                                % Width of image (meters)
h = [1.9283 1.938 1.9331 1.967 1.904 1.8653 1.812 1.812 1.7975 1.7539 ...
    1.749];                                        % Height of image (meters)
m = repmat(640, size(h));                          % Width of image (pixels)
n = repmat(364, size(h));                          % Height of image (pixels)
y = [-1.9142 -1.3476 -0.2898 0.1953 0.6468 0.261 0.2468 -0.0961 -0.5425 ...
    -1.0194 -1.7163];                              % X Center of image from origin (meters)
x = [1.0613 1.099 1.136 1.1408 1.0765 0.8031 1.1903 1.5184 1.5606 ...
    1.5301 1.4795];                                % Y Center of image from origin (meters)
u = [NaN NaN 202 195 203 257 158 106 101 NaN NaN]; % X pixel of antennae base
v = [NaN NaN 150 249 307 221 216 156 60 NaN NaN];  % Y pixel of antennae base
heading = [-0.0002 0.0053 0.0038 0.0032 0.0064 0.0 0.0009 0.0019 ...
    -0.0029 0.0 0.0];                              % in radians relative to origin heading

 
m0 = m ./ 2;                            % center pixel X
n0 = n ./ 2;                            % center pixel Y

du0 = u - m0;                           % X pixel of antennae offset from center
dv0 = n0 - v;                           % Y pixel of antennae offset from center

dx1 = du0 .* w ./ m;                     % X meters of antennae offset from center
dy1 = dv0 .* h ./ n;                     % Y meters of antennae offset from center

phi = atan2(dv0, du0);                  % Math angle to annotation in radians
theta = phi - heading ;                 % angle to annotation 
r = sqrt((dx1 .* dx1) + (dy1 .* dy1));   % Distance from center to annotation (meters)

dx2 = r .* cos(theta);                   % X offset from center rotated relative to heading
dy2 = r .* sin(theta);                   % Y offset from center rotated relative to heading

XX = x + dx2;                            % X meters from origin to annotation
YY = y + dy2;                            % Y meters from origin to annotation

fprintf(1, 'CenterX[m]\tCenterY[m]\tdX[pix]\tdY[pix]\tdirection[deg]\tradius[m]\tDistanceX\tDistanceY\n')
for i = 1:length(i)
   fprintf(1, '%5.2f\t%5.2f\t%3i\t%3i\t%5.2f\t%5.2f\t%5.2f\t%5.2f\n', ...
       x(i), y(i), du0(i), dv0(i), theta(i) * 180 / pi, r(i), XX(i), YY(i));
end